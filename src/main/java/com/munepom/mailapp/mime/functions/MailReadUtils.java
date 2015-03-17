package com.munepom.mailapp.mime.functions;


import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munepom.mailapp.dataset.MimeData;
import com.munepom.mailapp.functions.MailContentUtils;
import com.munepom.mailapp.functions.MailHeaderUtils;

public interface MailReadUtils extends MailHeaderUtils, MailContentUtils {

	Logger log = LoggerFactory.getLogger( MailReadUtils.class );

//	default MimeData readMimeMessage(MimeMessage mime) throws Exception {
//		return readMimeMessage(null, mime);
//	}
//
//	/**
//	 * メールを１通読み取り、DataSetMime オブジェクトに格納します。
//	 * 添付ファイルを書き出せるよう、メールが存在するディレクトリ Path も渡して起きます。
//	 * @param dirPath
//	 * @param mime
//	 * @return
//	 * @throws Exception
//	 */
//	default MimeData readMimeMessage(Path dirPath, MimeMessage mime) throws Exception {
//		MimeData dataSetMime = new MimeData();
//
//		// Header 処理
//		dataSetMime.setAllHeaders( mime.getAllHeaders() );
//
//		//Enumeration は、一旦読み取ると、元オブジェクトの読み取り位置も変更されるので、都度取得！
//		String contentType = getContentType(mime.getAllHeaders());
//		String charSet = getCharSet(contentType);
//		String contentTransferEncoding = getContentTransferEncoding(mime.getAllHeaders());
//
//		dataSetMime.setCharset( charSet );
//		dataSetMime.setContentType( contentType );
//		dataSetMime.setContentTransferEncoding( contentTransferEncoding );
//
//		// 処理の際には、InternetAddress へキャストすると、名前とメアドの双方が取得できる
//		dataSetMime.setFrom( mime.getFrom() );
//		dataSetMime.setReplyTo( mime.getReplyTo() );
//		dataSetMime.setTo( mime.getRecipients(Message.RecipientType.TO) );
//		dataSetMime.setCc( mime.getRecipients(Message.RecipientType.CC) );
//		dataSetMime.setBcc( mime.getRecipients(Message.RecipientType.BCC) );
//
//		// get subject
//		dataSetMime.setSubject( mime.getSubject() );
//
//		// コンテンツ取得
//		Object content = getContent(mime);
//		if(content instanceof byte[]) {
//			// Content-Type: plain/text で、 base64 エンコーディングの文字列だと、DecodingException BASE64Decoder ... という例外が発生するケースあり！
//			byte[] rawBody = (byte[]) content;
//			dataSetMime.setRawBody(rawBody);
//			dataSetMime.setBody( new String(rawBody) );
//		}
//		else if(content instanceof String) {
//			dataSetMime.setBody((String) content);
//		}
//		else if(content instanceof MimeMultipart) {
//			MimeMultipart multipart = (MimeMultipart) mime.getContent();
//			dataSetMime.setMultipart(multipart);
//			int i = 0;
//			int count = multipart.getCount();
//			List<String> multipartFileNames = new ArrayList<>();
//			for(i = 0; i < count; i++) {
//				// Stream を使うと、なぜか１つしか処理されなかった...
//				MimeBodyPart bodyPart = (MimeBodyPart) multipart.getBodyPart(i);
//				readMimeBodyPart(dirPath, bodyPart, multipartFileNames);
//			}
//			log.info("File num: {}", multipartFileNames.size());
//			dataSetMime.setMultipartFileNames(multipartFileNames.toArray(new String[0]));
//		}
//
//		return dataSetMime;
//	}
//
//	/**
//	 * MimeBodyPart を読み、添付ファイルを指定ディレクトリパスに書き出します。
//	 * ネストされた MimeBodyPart は、実際にありました...
//	 * @param dirPath
//	 * @param bodyPart
//	 * @param multipartFileNames
//	 * @throws Exception
//	 */
//	default void readMimeBodyPart(Path dirPath, MimeBodyPart bodyPart, List<String> multipartFileNames) throws Exception {
//		String disposition = bodyPart.getDisposition();
//		if( Objects.nonNull(disposition) && disposition.equalsIgnoreCase("ATTACHMENT") ) {
//			DataHandler handler = bodyPart.getDataHandler();
//			DataSource content = handler.getDataSource();
//			String fileName = handler.getName();
//			String mimeType = handler.getContentType();
//			log.info("File Name: {}", fileName);
//			log.info("File Type: {}", mimeType);
//
//			if( Objects.nonNull(dirPath) ) {
//				Path filePath = Paths.get(dirPath.toAbsolutePath().toString(), fileName);
//				try(
//					InputStream isContent = content.getInputStream();
//					OutputStream os =  Files.newOutputStream(filePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)
//				){
//					int reads = isContent.read();
//					while( reads != -1 ) {
//						os.write(reads);
//						reads = isContent.read();
//					}
//				}
//				log.info("File Path: {}", filePath.toAbsolutePath().toString() );
//			}
//
//			multipartFileNames.add(fileName);
//		}
//		else {
//			Object content = getContent(bodyPart);
//			if(content instanceof byte[]) {
//				// Content-Type: plain/text で、 base64 エンコーディングの文字列だと、DecodingException BASE64Decoder ... という例外が発生するケースあり！
//				byte[] rawBody = (byte[]) content;
//				log.info("Multipart Content: {}", new String(rawBody));
//			}
//			else if(content instanceof String) {
//				log.info("Multipart Content: {}", content);
//			}
//			else if(content instanceof MimeMultipart) {
//				//たまにある！
//				MimeMultipart multipart = (MimeMultipart) content;
//				int i = 0;
//				int count = multipart.getCount();
//				for(i = 0; i < count; i++) {
//					// Stream を使うと、なぜか１つしか処理されなかった...
//					MimeBodyPart nestedBodyPart = (MimeBodyPart) multipart.getBodyPart(i);
//					readMimeBodyPart(dirPath, nestedBodyPart, multipartFileNames);
//				}
//			}
//		}
//	}

//	default Path copyEmail(Path path, Session session, MimeMessage mime) {
//		Path dstFilePath = null;
//		String LF = "\n";
//
//		try {
//			// received date を任意の形式に変換できる
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//			Instant instant = null;
//			LocalDateTime ldt = null;
//
//			String fileName = "";
//
//			// ファイル名決定 (Message-Id を元に決める)
//			String id = mime.getMessageID();
//			MDC.put("mailId", id);
//			fileName = Optional.ofNullable( mime.getFileName() ).orElse(id.replaceAll("<|>",""));
//
//			instant = mime.getReceivedDate().toInstant();
//			ldt = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
//			String formattedLdt = ldt.format(formatter);
//
//			fileName = formattedLdt + "." + fileName;
//
//			Path dstDirPath = Paths.get(path.toAbsolutePath().toString(), fileName);
//			dstFilePath = Paths.get(dstDirPath.toAbsolutePath().toString(), fileName);
//			File dstFile = dstDirPath.toFile();
//			if( dstFile.exists() ) {
//				// 重複していたら、ファイル移動
//				// /tmp/メール受信日時.id/メール受信日時.id -> /tmp/メール受信日時.id.ファイル最終更新日時/メール受信日時.id.ファイル最終更新日時
//				instant = Instant.ofEpochMilli(dstDirPath.toFile().lastModified());
//				ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
//				String duplicatedDirName = fileName + "." + ldt.format(formatter);
//				Path duplicatedDirPath = Paths.get(path.toAbsolutePath().toString(), duplicatedDirName);
//				Files.move(dstDirPath, duplicatedDirPath, StandardCopyOption.ATOMIC_MOVE);
//			}
//
//			//保存用ディレクトリ作成
//			Files.createDirectory(dstDirPath);
//
//			try ( OutputStream os = Files.newOutputStream(dstFilePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE) ) {
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
//				try( InputStream is = mime.getInputStream() )
//				{
//					int reads = is.read();
//					while( reads != -1 ) {
//						os.write(reads);
//						reads = is.read();
//					}
//				}
//			}
//		}
//		catch (IOException | MessagingException e) {
//			sendErrorMail(session, mime);
//			log.error(e.getMessage(), e);
//			return null;
//		}
//		finally {
//			MDC.remove("mailId");
//		}
//
//		return dstFilePath;
//	}
//
//
//	//MimeMessage は Serialize 未実装なので、deep clone 不可能！
//	//MimeMessage[] コピーはできない。
//
//	/**
//	 * メールをコピーします
//	 * @param rootPath
//	 * @param fileName
//	 * @param mime
//	 * @return
//	 */
//	default boolean copyEmail(Path rootPath, String fileName, MimeMessage mime) {
//		return copyEmail(rootPath.toAbsolutePath().toString(), fileName, mime);
//	}
//
//	/**
//	 * メールをコピーします。
//	 * @param rootPath
//	 * @param fileName
//	 * @param mime
//	 * @return
//	 */
//	default boolean copyEmail(String rootPath, String fileName, MimeMessage mime) {
//		boolean result = false;
//
//		String LF = "\n";
//
//		Path dstPath = Paths.get(rootPath, fileName);
//		File dstFile = dstPath.toFile();
//		if( dstFile.exists() ) {
//			Instant instant = Instant.ofEpochMilli(dstPath.toFile().lastModified());
//			LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//			try {
//				Files.move(dstPath, Paths.get(rootPath, fileName + "." + ldt.format(formatter)), StandardCopyOption.ATOMIC_MOVE);
//			} catch (IOException e) {
//				log.error(e.getMessage(), e);
//			}
//		}
//
//		try ( OutputStream os = Files.newOutputStream(dstPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE) )
//		{
//			// 最初に、Header を保存
//			Collections.list( (Enumeration<?>) mime.getAllHeaderLines() ).stream()
//			.map(obj -> (String) obj)
//			.forEach( s -> {
//				try {
//					os.write(s.getBytes());
//					os.write(LF.getBytes());
//				} catch (Exception e) {
//					log.error(e.getMessage(), e);
//				}
////				System.out.println(obj);
////				Header header = (Header) obj;
////				System.out.println( header.getName()  +":" + header.getValue());
//			});
//			os.write(LF.getBytes());
//
//			// Header 以外のコンテンツは、下記で取得可能
//			try( InputStream is = mime.getInputStream() )
//			{
//				int reads = is.read();
//				while( reads != -1 ) {
//					os.write(reads);
//					reads = is.read();
//				}
//			}
//			catch( IOException e ) {
//				log.error(e.getMessage(), e);
//			}
//		} catch (IOException | MessagingException e) {
//			log.error(e.getMessage(), e);
//		}
//
//
//		return result;
//	}

}
