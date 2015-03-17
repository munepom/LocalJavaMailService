package com.munepom.mailapp.dataset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.activation.DataSource;

import lombok.Data;

/**
 *
 * MIME Content 要素
 * @author nishimura
 *
 */
@SuppressWarnings("serial")
@Data
public class MimeContentData implements Serializable {

	private String charset = "iso-2022-jp";
	private String contentType = "text/plain; charset=iso-2022-jp";
	private String contentTransferEncoding = "7bit";

	private String body;
	private byte[] rawBody;
	private DataSource dataSource;
	private String contentFileName;
	private String contentMimeType;
	private List<MimeContentData> contents;

	/**
	 * default constructor
	 */
	public MimeContentData(){
		// do nothing
	}

/* Utilities
---------------------------------------*/
	/**
	 * 本文が base64 文字列なら、true
	 * @return
	 */
	public boolean hasRawBody() {
		return Objects.nonNull(rawBody);
	}

	/**
	 * 添付ファイルが存在するなら、true
	 * @return
	 */
	public boolean hasFile() {
		return Objects.nonNull(dataSource);
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
