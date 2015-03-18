package com.munepom.mailapp.exception;

import javax.mail.Address;

/**
 *
 * 例外処理クラス
 *
 * @author nishimura
 *
 */
@SuppressWarnings("serial")
public class MailAppException extends Exception {

	/**
	 * エラーメール From
	 */
	protected Address[] errorFrom;

	public MailAppException() {
		super();
	}

	public MailAppException(String message) {
		super(message);
	}

	public MailAppException(String message, Throwable cause) {
		super(message, cause);
	}

	public MailAppException(Throwable cause) {
		super(cause);
	}

	protected MailAppException(String message, Throwable cause,
			boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
