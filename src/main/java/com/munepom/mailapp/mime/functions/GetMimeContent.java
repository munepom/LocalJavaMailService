package com.munepom.mailapp.mime.functions;


import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munepom.mailapp.mime.predicates.IsBase64BodyText;

/**
 *
 * MimePart から Contentを取得します
 *
 * byte[]:  本文が Base64 エンコーディング文字列の場合
 * String:  content が文字列の場合
 * Multipart:  content が Multipart の場合
 *
 * @author nishimura
 *
 */
public class GetMimeContent implements Function<MimePart, Object> {

	Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public Object apply(MimePart part) {
		IsBase64BodyText func = new IsBase64BodyText();

		if (func.test(part)) {
			// Content-Type: plain/text で、 base64 エンコーディングの文字列だと、DecodingException BASE64Decoder ... という例外が発生するケースあり！
			return getRawBody(part);
		}
		else {
			Object content = null;
			try {
				content = part.getContent();
			} catch (IOException | MessagingException e) {
				log.error(e.getMessage(), e);
			}

			if (content instanceof String) {
				return (String) content;
			}
			else if (content instanceof Multipart) {
				return (MimeMultipart) content;
			}
		}

		return null;
	}

	/**
	 * body part を byte[] 型で取得します
	 * @param part
	 * @return
	 */
	private byte[] getRawBody(MimePart part) {
		byte[] rawBody = null;

		try ( InputStream is = part.getInputStream() ) {
			rawBody = new byte[is.available()];
			is.read(rawBody);
		} catch (IOException | MessagingException e) {
			log.error(e.getMessage(), e);
		}

		return rawBody;
	}
}
