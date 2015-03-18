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
	public void accept(Session session, MimeData mimeData) {
		try {
			MimeMessage message = new MimeMessage(session);
			String charset = mimeData.getCharset();

			String contentType = mimeData.getContentType();
			String contentTransferEncoding = mimeData.getContentTransferEncoding();
			message.addHeader("Content-Type", contentType);
			message.addHeader("Content-Transfer-Encoding", contentTransferEncoding);

			//lambda 式を使うと、内部で発生する例外キャッチ処理で結局めんどかった...
			List<Address> from = mimeData.getFrom();
			List<Address> replyTo = mimeData.getReplyTo();
			List<Address> to = mimeData.getTo();
			List<Address> cc = mimeData.getCc();
			List<Address> bcc = mimeData.getBcc();

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

			message.setSubject(mimeData.getSubject(), charset);

			byte[] rawBody = mimeData.getRawBody();
			String body = mimeData.getBody();
			Multipart multipart = mimeData.getMultipart();
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

}
