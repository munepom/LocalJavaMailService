package com.munepom.mailapp;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 *
 * メール取得クラス<br />
 * Folder からメールを取得し、コピーして、コピーされたメールの Path をリストとして出力します。
 * TODO: Path などは、データエレメント定義で渡せるようにする
 *
 * @author munepom
 *
 */
@Data
public class MailReader implements Function<Folder, List<Path>> {

	Logger log = LoggerFactory.getLogger(getClass());

	protected PropsSetMailServer propsSet;

	protected Path appHomePath;
	protected Path tmpPath;
	protected Path failedPath;

	protected int folderOpenMode = Folder.READ_ONLY;
	protected boolean canDeleteMail = false;

	protected boolean isParallel;

	public MailReader() {
	}

	public MailReader(PropsSetMailServer propsSet, Path appHomePath, Path tmpPath, Path failedPath, int folderOpenMode, boolean canDeleteMail, boolean isParallel) {
		this.propsSet = propsSet;
		this.appHomePath = appHomePath;
		this.tmpPath = tmpPath;
		this.failedPath = failedPath;
		this.folderOpenMode = folderOpenMode;
		this.canDeleteMail = canDeleteMail;
		this.isParallel = isParallel;
	}

	/**
	 * Folder からメールリストを取得し、アプリのメール退避パスへコピーし、その Path のリストを返します。
	 */
	@Override
	public List<Path> apply(Folder folder) {
		Properties props = propsSet.getMailProps();
		String user = propsSet.getUser();
		String password = propsSet.getPassword();
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});

		try {
			// open the folder
			folder.open(folderOpenMode);

			// メール読み出し & コピー & キュー渡し
			// folder.close() の前に リストをコピーしておかないと、javax.mail.FolderClosedException が発生する！
			// com.sun.mail.imap.IMAPMessage が Serializable を実装していないので、オブジェクトコピーは無理。
			// 一旦ファイルに書き出し、ファイル名を Queue に渡して処理させる。
			Stream<Message> stream = null;
			if( isParallel ) {
				stream = Stream.of( Optional.ofNullable(folder.getMessages()).orElse(new Message[0]) ).parallel();
			}
			else {
				stream = Stream.of( Optional.ofNullable(folder.getMessages()).orElse(new Message[0]) );
			}

			return stream
			.filter(message -> {
				try {
					return ! message.isSet(Flags.Flag.DELETED);
				} catch (Exception e) {
					//TODO: 管理者へメール
					log.error(e.getMessage(), e);
				}
				return false;
			})
			.map(message -> (MimeMessage) message)
			.map(mime -> {
				Path copiedPath = copyEmail(tmpPath, session, mime);
				if( Objects.isNull(copiedPath) ) {
					copyEmail(failedPath, session, mime);  //メールコピー失敗時は、失敗パスへコピー。
				}
				else if(canDeleteMail) {
					try {
						mime.setFlag(Flags.Flag.DELETED, true);
					} catch (Exception e) {
						// TODO: 管理者へメール
						e.printStackTrace();
					}
				}
				return copiedPath;
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
		} catch (MessagingException e1) {
			// TODO 管理者へメール
			e1.printStackTrace();
		}
		finally {
			if( folder != null ) {
				try {
					folder.close(true);	// true で閉じると、delete フラグのメールが削除される
				} catch (MessagingException e) {
					// TODO 管理者へメール
					log.error(e.getMessage(), e);
				}
			}
		}

		return null;
	}

	/**
	 *
	 * メールを指定パスへコピーします。<br />
	 *
	 * @param path: メール一時保存ルートパス
	 * @param session
	 * @param mime
	 * @return
	 */
	private Path copyEmail(Path path, Session session, MimeMessage mime) {
		Path dstFilePath = null;
		String LF = "\n";

		try {
			// received date を任意の形式に変換できる
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			Instant instant = null;
			LocalDateTime ldt = null;

			String fileName = "";

			// ファイル名決定 (Message-Id を元に決める)
			String id = mime.getMessageID();
			MDC.put("mailId", id);
			fileName = Optional.ofNullable( mime.getFileName() ).orElse(id.replaceAll("<|>",""));

			instant = mime.getReceivedDate().toInstant();
			ldt = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
			String formattedLdt = ldt.format(formatter);

			fileName = formattedLdt + "." + fileName;

			Path dstDirPath = Paths.get(path.toAbsolutePath().toString(), fileName);
			dstFilePath = Paths.get(dstDirPath.toAbsolutePath().toString(), fileName);
			File dstFile = dstDirPath.toFile();
			if( dstFile.exists() ) {
				// 重複していたら、ファイル移動
				// /tmp/メール受信日時.id/メール受信日時.id -> /tmp/メール受信日時.id.ファイル最終更新日時/メール受信日時.id.ファイル最終更新日時
				instant = Instant.ofEpochMilli(dstDirPath.toFile().lastModified());
				ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
				String duplicatedDirName = fileName + "." + ldt.format(formatter);
				Path duplicatedDirPath = Paths.get(path.toAbsolutePath().toString(), duplicatedDirName);
				Files.move(dstDirPath, duplicatedDirPath, StandardCopyOption.ATOMIC_MOVE);
			}

			//保存用ディレクトリ作成
			Files.createDirectory(dstDirPath);

			try ( OutputStream os = Files.newOutputStream(dstFilePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE) ) {
				mime.writeTo(os);
//				// 最初に、Header を保存
//				Collections.list( (Enumeration<?>) mime.getAllHeaders() ).stream()
//				.map(obj -> (Header) obj)
//				.forEach(header -> {
//					log.debug("{}: {}", header.getName(), header.getValue());
//					try {
//						os.write((header.getName() + ": " + header.getValue()).getBytes());
//						os.write(LF.getBytes());
//					} catch (IOException e) {
//						throw new UncheckedIOException(e);
//					}
//				});
//				os.write(LF.getBytes());
//
//				// Header 以外のコンテンツは、下記で取得可能
				// ただし、これをやると、base64 エンコーディングだった場合、次は RawInputStream でないと復元できない？
//				try( InputStream is = mime.getInputStream() )
//				{
//					int reads = is.read();
//					while( reads != -1 ) {
//						os.write(reads);
//						reads = is.read();
//					}
//				}
			}
		}
		catch (IOException | MessagingException e) {
			//TODO 管理者へメール
			//XXX ユーザへメールはしない。
			log.error(e.getMessage(), e);
			return null;
		}
		finally {
			MDC.remove("mailId");
		}

		return dstFilePath;
	}

}
