package com.munepom.mailapp.mime.functions;


import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munepom.mailapp.dataset.MimeData;

/**
 * メールを１通読み取り、DataSetMime オブジェクトに格納します。
 * 添付ファイルを書き出せるよう、メールが存在するディレクトリ Path も渡しておきます。
 * @author nishimura
 *
 */
public class ReadMimeMessage implements BiFunction<MimeMessage, Path, MimeData>{

	Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public MimeData apply(MimeMessage mime, Path parentPath) {
		if (Objects.isNull(mime)) {
			return null;
		}

		MimeData dataSetMime = new MimeData();
		dataSetMime.setParentPath(parentPath);
		try {
			// Header 処理
			dataSetMime.setAllHeaders( mime.getAllHeaders() );

			//Enumeration は、一旦読み取ると、元オブジェクトの読み取り位置も変更されるので、都度取得！
			GetContentType funcContentType = new GetContentType();
			GetCharSet funcCharSet = new GetCharSet();
			GetContentTransferEncoding funcCTE = new GetContentTransferEncoding();
			String contentType = funcContentType.apply(mime);
			String charset = funcCharSet.apply(mime);
			String contentTransferEncoding = funcCTE.apply(mime);

			dataSetMime.setCharset(charset);
			dataSetMime.setContentType(contentType);
			dataSetMime.setContentTransferEncoding(contentTransferEncoding);

			// 処理の際には、InternetAddress へキャストすると、名前とメアドの双方が取得できる
			dataSetMime.setFrom(convert2List(mime.getFrom()));
			dataSetMime.setReplyTo(convert2List(mime.getReplyTo()));
			dataSetMime.setTo(convert2List(mime.getRecipients(Message.RecipientType.TO)));
			dataSetMime.setCc(convert2List(mime.getRecipients(Message.RecipientType.CC)));
			dataSetMime.setBcc(convert2List(mime.getRecipients(Message.RecipientType.BCC)));

			// get subject
			dataSetMime.setSubject(mime.getSubject());

			// Multipart なら、全体を保持しておく
			GetMimeContent funcMimeContent = new GetMimeContent();
			Object content = funcMimeContent.apply(mime);
			if (content instanceof MimeMultipart) {
				dataSetMime.setMultipart((MimeMultipart) content);
			}

			// コンテンツ取得
			ReadMimeContent consumerMimeContent = new ReadMimeContent();
			consumerMimeContent.accept(mime, dataSetMime);
		}
		catch (MessagingException e) {
			log.error(e.getMessage(), e);
		}

		return dataSetMime;
	}

	private List<Address> convert2List(Address[] addresses) {
		return Optional.ofNullable(addresses).map(arr -> Arrays.asList(arr)).orElse(null);
	}

}
