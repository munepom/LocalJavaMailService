package com.munepom.mailapp.functions;


import java.util.Collections;
import java.util.Enumeration;
import java.util.Optional;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

/**
 *
 * メールヘッダユーティリティ
 * @author nishimura
 *
 */
public interface MailHeaderUtils {

	default String getContentType(MimeMessage mime) throws MessagingException {
		return getContentType(
					Optional.ofNullable( mime.getHeader("Content-Type") )
					.filter(arr -> arr.length > 0)
					.map(arr -> arr[0])
				);
	}

	default String getContentType(MimeBodyPart part) throws MessagingException {
		return getContentType(
					Optional.ofNullable( part.getHeader("Content-Type") )
					.filter(arr -> arr.length > 0)
					.map(arr -> arr[0])
				);
	}

	default String getContentType(Enumeration<?> headers) throws MessagingException {
		return getContentType(
					Collections.list( headers ).stream()
					.map(obj -> (Header) obj)
					.filter(header -> header.getName().equals("Content-Type"))
					.map(header -> header.getValue())
					.findFirst()
				);
	}

	default String getContentType(Optional<String> optional) throws MessagingException {
		return optional.orElse("text/plain; charset=iso-2022-jp");
	}

	/**
	 * 文字エンコーディングを取得します
	 * @param mime
	 * @return
	 * @throws MessagingException
	 */
	default String getCharSet(MimeMessage mime) throws MessagingException {
		return getCharSet( getContentType(mime) );
	}

	default String getCharSet(MimeBodyPart part) throws MessagingException {
		return getCharSet( getContentType(part) );
	}

	default String getCharSet(Enumeration<?> headers) throws MessagingException {
		return getCharSet( getContentType(headers) );
	}

//	default String getCharSet(String[] headerContentType) throws MessagingException {
//		String contentType = Optional.ofNullable( headerContentType ).filter(arr -> arr.length > 0).map(arr -> arr[0]).orElse("");
//		return getCharSet(contentType);
//	}

	default String getCharSet(String contentType) throws MessagingException {
		return Optional.ofNullable( contentType.split("charset=") )
				.filter(arr -> arr.length > 1)
				.map(arr -> arr[1])
				.map(s -> s.replaceAll("\"", ""))
				.orElse("");
	}

	/**
	 * Content-Transfer-Encoding を取得します
	 * @param mime
	 * @return
	 * @throws MessagingException
	 */
	default String getContentTransferEncoding(MimeMessage mime) throws MessagingException {
		return getContentTransferEncoding(
					Optional.ofNullable( mime.getHeader("Content-Transfer-Encoding") )
					.filter(arr -> arr.length > 0)
					.map(arr -> arr[0])
					,
					getCharSet(mime)
				);
	}

	default String getContentTransferEncoding(MimeBodyPart part) throws MessagingException {
		return getContentTransferEncoding(
					Optional.ofNullable( part.getHeader("Content-Transfer-Encoding") )
					.filter(arr -> arr.length > 0)
					.map(arr -> arr[0])
					,
					getCharSet(part)
				);
	}

	/**
	 * 一旦読み取られた Enumeration は使わないこと！
	 * @param headers
	 * @return
	 * @throws MessagingException
	 */
	default String getContentTransferEncoding(Enumeration<?> headers) throws MessagingException {
		return getContentTransferEncoding(
					Collections.list( headers ).stream()
					.map(obj -> (Header) obj)
					.filter(header -> header.getName().equals("Content-Transfer-Encoding"))
					.map(header -> header.getValue())
					.findFirst(),
					getCharSet(headers)
				);
	}

	default String getContentTransferEncoding(Optional<String> optional, String charSet) throws MessagingException {
		return optional.orElse(charSet.equalsIgnoreCase("iso-2022-jp") ? "7bit" : "");
	}

	/**
	 * Content-Transfer-Encoding が 7bit なら、true
	 * @param mime
	 * @return
	 * @throws MessagingException
	 */
	default boolean is7bit(MimeMessage mime) throws MessagingException {
		return getContentTransferEncoding(mime).equalsIgnoreCase("7bit");
	}

	default boolean is7bit(MimeBodyPart part) throws MessagingException {
		return getContentTransferEncoding(part).equalsIgnoreCase("7bit");
	}

	/**
	 * Content-Type が text/plain で、Content-Transfer-Encoding が base64 なら、true
	 * @param mime
	 * @return
	 * @throws MessagingException
	 */
	default boolean isBase64BodyText(MimeMessage mime) throws MessagingException {
		return mime.getContentType().contains( "text" ) && getContentTransferEncoding(mime).equalsIgnoreCase("base64");
	}

	default boolean isBase64BodyText(MimeBodyPart part) throws MessagingException {
		return part.getContentType().contains( "text" ) && getContentTransferEncoding(part).equalsIgnoreCase("base64");
	}
}
