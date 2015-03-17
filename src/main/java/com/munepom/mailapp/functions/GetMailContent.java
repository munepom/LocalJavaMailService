package com.munepom.mailapp.functions;


import java.io.IOException;
import java.io.InputStream;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface GetMailContent extends MailHeaderUtils {

	Logger log = LoggerFactory.getLogger( GetMailContent.class );

	/**
	 * メール Content を取得します
	 * @param part
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	default Object getContent(MimeBodyPart part) throws MessagingException, IOException {
		// コンテンツ取得
		if( isBase64BodyText(part) ) {
			// Content-Type: plain/text で、 base64 エンコーディングの文字列だと、DecodingException BASE64Decoder ... という例外が発生するケースあり！
			byte[] rawBody = null;

			try ( InputStream is = part.getInputStream() ) {
				rawBody = new byte[is.available()];
				is.read(rawBody);
			} catch (IOException | MessagingException e) {
				log.error(e.getMessage(), e);
			}

			return rawBody;
		}
		else {
			Object content = part.getContent();
			if(content instanceof String) {
				return (String) content;
			}
			else if(content instanceof Multipart) {
				return (MimeMultipart) content;
			}
		}

		return null;
	}

	/**
	 * メール Content を取得します
	 * @param message
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	default Object getContent(MimeMessage message) throws MessagingException, IOException {
		// コンテンツ取得
		if( isBase64BodyText(message) ) {
			// Content-Type: plain/text で、 base64 エンコーディングの文字列だと、DecodingException BASE64Decoder ... という例外が発生するケースあり！
			byte[] rawBody = null;

			try ( InputStream is = message.getInputStream() ) {
				rawBody = new byte[is.available()];
				is.read(rawBody);
			} catch (IOException | MessagingException e) {
				log.error(e.getMessage(), e);
			}

			return rawBody;
		}
		else {
			Object content = message.getContent();
			if(content instanceof String) {
				return (String) content;
			}
			else if(content instanceof Multipart) {
				return (MimeMultipart) content;
			}
		}

		return null;
	}

//
//	default DataSetMime readMimeMessage(MimeMessage mime) {
//
//		DataSetMime dataSetMime = new DataSetMime();
//
//		try {
//			// Header 処理
//			dataSetMime.setAllHeaders( mime.getAllHeaders() );
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
//			Object content = getMimeMessageContent(mime);
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
//
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
