package com.munepom.mailapp;

import java.io.Serializable;
import java.util.Properties;

import lombok.Data;

@SuppressWarnings("serial")
@Data
public class MailServerProps implements Serializable {

	/**
	 * メール読み出し用プロパティ
	 */
	private Properties mailProps;

	/**
	 * ホスト名
	 */
	private String host;

	/**
	 * メールボックスユーザ名
	 */
	private String user;

	/**
	 * メールボックスログイン用パスワード
	 */
	private String password;

	/**
	 * プロトコル名 (imaps など)
	 */
	private String protocol;

	/**
	 * メールボックス名
	 */
	private String mbox = "INBOX";

	public MailServerProps(){

	}

	public MailServerProps(Properties mailProps, String host, String user, String password, String protocol, String mbox) {
		this.mailProps = mailProps;
		this.host = host;
		this.user = user;
		this.password = password;
		this.protocol = protocol;
		this.mbox = mbox;
	}

}
