package com.munepom.mailapp;

import javax.mail.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailAppException extends Exception {

	Logger log = LoggerFactory.getLogger( this.getClass() );

	protected String charset = "iso-2022-jp";
	protected String contentType = "text/plain; charset=iso-2022-jp";
	protected String contentTransferEncoding = "7bit";

	protected Address[] from;
	protected Address[] to;
	protected String subject;
	protected String body;

	/**
	 * defualt constructor
	 */
	public MailAppException(){
		super();
	}

	public MailAppException(String message) {
		log.error(message);
	}

	public MailAppException(Throwable cause) {
		log.error(cause.getMessage(), cause);
	}

	public MailAppException(String message, Throwable cause) {
		log.error(message, cause);
	}

}
