package com.munepom.mailapp.mime.functions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.function.BiConsumer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munepom.mailapp.dataset.MimeContentData;
import com.munepom.mailapp.dataset.MimeData;

/**
 *
 * MimePart を読み込み、#com.munepom.mailapp.dataset.MimeData へ変換します
 *
 * @author nishimura
 *
 */
public class ReadMimeContent implements BiConsumer<MimePart, MimeData> {

	Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void accept(MimePart part, MimeData mimeData) {
		GetMimeContent funcMimeContent = new GetMimeContent();
		Object content = funcMimeContent.apply(part); // byte[]  or  String  or MimeMultipart  or null
		if (content instanceof byte[]) {
			// Content-Type: plain/text で、 base64 エンコーディングの文字列だと、DecodingException BASE64Decoder ... という例外が発生するケースあり！
			byte[] rawBody = (byte[]) content;
			mimeData.setRawBody(rawBody);
			mimeData.setBody(new String(rawBody));
		}
		else if(content instanceof String) {
			mimeData.setBody((String) content);
		}
		else if(content instanceof MimeMultipart) {
			MimeMultipart multipart = (MimeMultipart) content;
			int i = 0;
			int count;
			try {
				count = multipart.getCount();
				for(i = 0; i < count; i++) {
					// Stream を使うと、なぜか１つしか処理されなかった...
					MimeBodyPart bodyPart = (MimeBodyPart) multipart.getBodyPart(i);
					mimeData.addContent(readMimeBodyPart(bodyPart, mimeData.getParentPath()));
				}
			} catch (MessagingException e) {
				log.error(e.getMessage(), e);
			}
		}
		else {
			// case null ???
		}

	}

	/**
	 * MimeBodyPart を読み、添付ファイル保存指定ディレクトリパスが存在する場合は、書き出します。
	 * ネストされた MimeBodyPart は、実際にあります...
	 * @param dirPath
	 * @param bodyPart
	 * @param multipartFileNames
	 * @throws Exception
	 */
	private MimeContentData readMimeBodyPart(MimePart part, Path parentPath) {
		MimeContentData contentData = new MimeContentData();

		GetContentType funcContentType = new GetContentType();
		GetCharSet funcCharSet = new GetCharSet();
		GetContentTransferEncoding funcCTE = new GetContentTransferEncoding();
		String contentType = funcContentType.apply(part);
		String charset = funcCharSet.apply(part);
		String contentTransferEncoding = funcCTE.apply(part);

		try {
			String disposition = part.getDisposition();
			if( Objects.nonNull(disposition) && disposition.equalsIgnoreCase("ATTACHMENT") ) {
				// 添付ファイルの場合
				DataHandler handler = part.getDataHandler();
				DataSource content = handler.getDataSource();
				String fileName = handler.getName();
				String mimeType = handler.getContentType();
				log.info("Attachment File Name: {}", fileName);
				log.info("Attachment File Type: {}", mimeType);

				if( Objects.nonNull(parentPath) ) {
					// メール保存ディレクトリパスがあれば、添付ファイルを保存する。
					Path filePath = Paths.get(parentPath.toAbsolutePath().toString(), fileName);
					try( InputStream isContent = content.getInputStream();
						OutputStream os =  Files.newOutputStream(filePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE))
					{
						int reads = isContent.read();
						while( reads != -1 ) {
							os.write(reads);
							reads = isContent.read();
						}
					} catch (IOException e) {
						log.error(e.getMessage(), e);
					}
					log.info("File Path: {}", filePath.toAbsolutePath().toString() );
				}

				contentData.setContentType(contentType);
				contentData.setContentTransferEncoding(contentTransferEncoding);
				contentData.setDataSource(content);
				contentData.setContentFileName(fileName);
				contentData.setContentMimeType(mimeType);
			}
			else {
				GetMimeContent func = new GetMimeContent();
				Object content = func.apply(part);
				if(content instanceof byte[]) {
					// Content-Type: plain/text で、 base64 エンコーディングの文字列だと、DecodingException BASE64Decoder ... という例外が発生するケースあり！
					byte[] rawBody = (byte[]) content;
					log.debug("Multipart Content: {}", new String(rawBody));

					contentData.setContentType(contentType);
					contentData.setCharset(charset);
					contentData.setContentTransferEncoding(contentTransferEncoding);
					contentData.setRawBody(rawBody);
					contentData.setBody(new String(rawBody));

					return contentData;
				}
				else if(content instanceof String) {
					log.debug("Multipart Content: {}", content);

					contentData.setContentType(contentType);
					contentData.setCharset(charset);
					contentData.setContentTransferEncoding(contentTransferEncoding);
					contentData.setBody((String) content);

					return contentData;
				}
				else if(content instanceof MimeMultipart) {
					//たまにある！
					MimeMultipart multipart = (MimeMultipart) content;
					int i = 0;
					int count = multipart.getCount();
					for(i = 0; i < count; i++) {
						// Stream を使うと、なぜか１つしか処理されなかった...
						MimeBodyPart nestedBodyPart = (MimeBodyPart) multipart.getBodyPart(i);
						contentData.addContent(readMimeBodyPart(nestedBodyPart, parentPath));
					}
				}
			}
		}
		catch (MessagingException e) {
			log.error(e.getMessage(), e);
		}

		return contentData;
	}

}
