package com.munepom.mailapp.dataset;

import javax.mail.Address;
import javax.mail.Multipart;

public class DataSetErrorMail {

	protected String charset = "iso-2022-jp";
	protected String contentType = "text/plain; charset=iso-2022-jp";
	protected String contentTransferEncoding = "7bit";

	protected Address[] from;
	protected Address[] replyTo;
	protected Address[] to;
	protected Address[] cc;
	protected Address[] bcc;
	protected String   subject;
	protected String body;
	protected byte[] rawBody;
	protected Multipart multipart;

	public DataSetErrorMail(){}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setContentTransferEncoding(String contentTransferEncoding) {
		this.contentTransferEncoding = contentTransferEncoding;
	}

	public void setFrom(Address[] from) {
		this.from = from;
	}

	public void setReplyTo(Address[] replyTo) {
		this.replyTo = replyTo;
	}

	public void setTo(Address[] to) {
		this.to = to;
	}

	public void setCc(Address[] cc) {
		this.cc = cc;
	}

	public void setBcc(Address[] bcc){
		this.bcc = bcc;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setRawBody(byte[] rawbody) {
		this.rawBody = rawbody;
	}

	public void setMultipart(Multipart multipart) {
		this.multipart = multipart;
	}

	public String getCharset() {
		return charset;
	}

	public String getContentType() {
		return contentType;
	}

	public String getContentTransferEncoding() {
		return contentTransferEncoding;
	}

	public Address[] getFrom() {
		return from;
	}

	public Address[] getReplyTo() {
		return replyTo;
	}

	public Address[] getTo() {
		return to;
	}

	public Address[] getCc() {
		return cc;
	}

	public Address[] getBcc() {
		return bcc;
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	public byte[] getRawBody() {
		return rawBody;
	}

	public Multipart getMultipart() {
		return multipart;
	}
}
