package com.munepom.mailapp.dataset;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import javax.mail.Address;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * MIME 要素
 * @author nishimura
 *
 */
@SuppressWarnings("serial")
@Data
public class MimeData implements Serializable {

	Logger log = LoggerFactory.getLogger( this.getClass() );

/* メール復元用
-----------------------------------------------------*/
//	private MimeMessage message;  //これは、メールのシャローコピーとなるケースが多いので、使わない
	private Enumeration<?> allHeaders;
	private MimeMultipart multipart;
	/**
	 * メール保存ディレクトリパス
	 */
	private Path parentPath;

/* メールパース結果
-----------------------------------------------------*/
	private String charset = "iso-2022-jp";
	private String contentType = "text/plain; charset=iso-2022-jp";
	private String contentTransferEncoding = "7bit";

	private List<Address> from;
	private List<Address> replyTo;
	private List<Address> to;
	private List<Address> cc;
	private List<Address> bcc;
	private String   subject;
	private String   body;
	private byte[]  rawBody;
	private List<MimeContentData> contents;

	public MimeData(){}

/* Utilities
-----------------------------------------------------*/
	/**
	 * 本文が base64 文字列なら、true
	 * @return
	 */
	public boolean hasRawBody() {
		return Objects.nonNull(rawBody);
	}

	/**
	 * {@link #contents} へ要素を追加します
	 * @param content
	 */
	public void addContent(MimeContentData content) {
		if (Objects.isNull(contents)) {
			contents = new ArrayList<>();
		}
		this.contents.add(content);
	}

}