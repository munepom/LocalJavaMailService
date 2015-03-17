package com.munepom.mailapp.exception;


import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munepom.mailapp.dataset.MimeData;

public class SendErrorMail implements BiConsumer<Session, MimeData>{

	Logger log = LoggerFactory.getLogger( SendErrorMail.class );

	/**
	 * アプリ管理者へエラーメール送信
	 * @param mime
	 */
	@Override
	public void accept(Session session, MimeData dataSetMime) {
		try {
			MimeMessage message = new MimeMessage(session);
			String charset = dataSetMime.getCharset();

			String contentType = dataSetMime.getContentType();
			String contentTransferEncoding = dataSetMime.getContentTransferEncoding();
			message.addHeader("Content-Type", contentType);
			message.addHeader("Content-Transfer-Encoding", contentTransferEncoding);

			//lambda 式を使うと、内部で発生する例外キャッチ処理で結局めんどい...
			List<Address> from = dataSetMime.getFrom();
			List<Address> replyTo = dataSetMime.getReplyTo();
			List<Address> to = dataSetMime.getTo();
			List<Address> cc = dataSetMime.getCc();
			List<Address> bcc = dataSetMime.getBcc();

			if( Objects.nonNull(from) ) {
				message.addFrom(from.toArray(new Address[0]));
			}
			if( Objects.nonNull(replyTo) ) {
				message.setReplyTo(replyTo.toArray(new Address[0]));
			}
			if( Objects.nonNull(to) ) {
				message.addRecipients( RecipientType.TO, to.toArray(new Address[0]) );
			}
			if( Objects.nonNull(cc) ) {
				message.addRecipients( RecipientType.CC, cc.toArray(new Address[0]) );
			}
			if( Objects.nonNull(bcc) ) {
				message.addRecipients( RecipientType.BCC, bcc.toArray(new Address[0]) );
			}

			message.setSubject(dataSetMime.getSubject(), charset);

			byte[] rawBody = dataSetMime.getRawBody();
			String body = dataSetMime.getBody();
			Multipart multipart = dataSetMime.getMultipart();
			if( Objects.nonNull(rawBody) ) {
				//base64 エンコーディング文字列対策
				message.setContent( new String( (byte[]) rawBody, charset ), contentType );
			}
			else if( Objects.nonNull(body) ) {
				message.setText(body, charset);
			}
			else if( Objects.nonNull(multipart) ) {
				message.setContent(multipart);
			}

			Transport.send(message);
		}
		catch(MessagingException | IOException e) {
			log.error(e.getMessage(), e);
		}
	}

//	/**
//	 * メール送信者へエラーメール送信
//	 * @param mime
//	 */
//	default void sendErrorMail(Session session, MimeMessage mimeOrg) {
//		try {
//			MimeMessage message = new MimeMessage(session);
//			String charset = getCharSet(mimeOrg);
//
//			String contentType = mimeOrg.getContentType();
//			message.addHeader("Content-Type", contentType);
//			message.addHeader("Content-Transfer-Encoding", getContentTransferEncoding(mimeOrg));
//
//			message.addFrom( new InternetList<Address>{ new InternetAddress("nishimura@asd-inc.co.jp", "にしむら From", charset) } );
//			message.setReplyTo( new InternetList<Address>{ new InternetAddress("nishimura@asd-inc.co.jp", "にしむら Reply-To", charset) } );
//			message.addRecipients( RecipientType.TO, new InternetList<Address>{ new InternetAddress("nishimura@asd-inc.co.jp", "にしむら To", charset) });
////			mime.addRecipients( RecipientType.TO, mimeOrg.getRecipients(RecipientType.TO));
////			mime.addRecipients( RecipientType.CC, mimeOrg.getRecipients(RecipientType.CC));
////			mime.addRecipients( RecipientType.BCC, mimeOrg.getRecipients(RecipientType.BCC));
//
//			message.setSubject(mimeOrg.getSubject(), charset);
//
//			Object content = getContent(mimeOrg);
//			if( content instanceof byte[] ) {
//				//base64 エンコーディング文字列対策
//				message.setContent( new String( (byte[]) content, charset ), contentType );
//			}
//			else if(content instanceof String) {
//				message.setText((String) content, charset);
//			}
//			else if(content instanceof Multipart) {
//				message.setContent((Multipart) content);
//			}
//
//			Transport.send(message);
//		}
//		catch(MessagingException | IOException e) {
//			log.error(e.getMessage(), e);
//		}
//
//	}

//	/**
//	 *
//	 * @param mime
//	 * @return byte[] or String or MimeMultipart
//	 * @throws MessagingException
//	 * @throws IOException
//	 */
//	default Object getMimeMessageContent(Message mime) throws MessagingException, IOException {
//		// コンテンツ取得
//		if( isBase64BodyText(mime) ) {
//			// Content-Type: plain/text で、 base64 エンコーディングの文字列だと、DecodingException BASE64Decoder ... という例外が発生するケースあり！
//			byte[] rawBody = null;
//
//			try ( InputStream is = mime.getInputStream() ) {
//				rawBody = new byte[is.available()];
//				is.read(rawBody);
//			} catch (IOException | MessagingException e) {
//				log.error(e.getMessage(), e);
//			}
//
//			return rawBody;
//		}
//		else {
//			Object content = mime.getContent();
//			if(content instanceof String) {
//				return (String) content;
//			}
//			else if(content instanceof Multipart) {
//				return (MimeMultipart) content;
//			}
//		}
//
//		return null;
//	}

//	default DataSetMime readMimeMessage(MimeMessage mime) {
//
//		DataSetMime dataSetMime = new DataSetMime();
//
//		try {
//			// Header 処理
//			dataSetMime.setAllHeaders( mime.getAllHeaders() );
//
//			//Enumeration は、一旦読み取ると、元オブジェクトの読み取り位置も変更されるので、都度取得！
//			String contentType = getContentType(mime.getAllHeaders());
//			String charSet = getCharSet(contentType);
//			String contentTransferEncoding = getContentTransferEncoding(mime.getAllHeaders());
//
//			dataSetMime.setCharset( charSet );
//			dataSetMime.setContentType( contentType );
//			dataSetMime.setContentTransferEncoding( contentTransferEncoding );
//
//			// 処理の際には、InternetAddress へキャストすると、名前とメアドの双方が取得できる
//			dataSetMime.setFrom( mime.getFrom() );
//			dataSetMime.setReplyTo( mime.getReplyTo() );
//			dataSetMime.setTo( mime.getRecipients(Message.RecipientType.TO) );
//			dataSetMime.setCc( mime.getRecipients(Message.RecipientType.CC) );
//			dataSetMime.setBcc( mime.getRecipients(Message.RecipientType.BCC) );
//
//			// get subject
//			dataSetMime.setSubject( mime.getSubject() );
//
//			// コンテンツ取得
//			Object content = getContent(mime);
//			if(content instanceof byte[]) {
//				// Content-Type: plain/text で、 base64 エンコーディングの文字列だと、DecodingException BASE64Decoder ... という例外が発生するケースあり！
//				byte[] rawBody = (byte[]) content;
//				dataSetMime.setRawBody(rawBody);
//				dataSetMime.setBody( new String(rawBody) );
//			}
//			else if(content instanceof String) {
//				dataSetMime.setBody((String) content);
//			}
//			else if(content instanceof Multipart) {
//				dataSetMime.setMultipart((MimeMultipart) mime.getContent());
//			}
////		}
////		catch (DecodingException e) {
////			if( e.getMessage().contains("BASE64Decoder") ) {
////				try ( InputStream is = mime.getRawInputStream() ) {
////					byte[] content = new byte[is.available()];
////					is.read(content);
////					dataSetMime.setBody(new String(content));
////				} catch (IOException | MessagingException e1) {
////					log.error(e.getMessage(), e);
////				}
////			}
//		} catch (MessagingException | IOException e) {
//			log.error(e.getMessage(), e);
//		}
//
//		return dataSetMime;
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
