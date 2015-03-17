package com.munepom.mailapp;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.function.Consumer;

import lombok.Data;

/**
 *
 * メールパスからメールオブジェクトを復元し、処理を行う抽象クラス
 * @author munepom
 *
 */
@Data
public abstract class MailConsumer implements Runnable, Consumer<Path> {

	protected PropsSetMailServer propsSet;

	protected Path appHomePath;
	protected Path tmpPath;
	protected Path failedPath;

	protected Path mailPath;

	/**
	 * default constructor
	 */
	public MailConsumer() {
		// do nothing
	}

	public MailConsumer(PropsSetMailServer propsSet, Path appHomePath, Path tmpPath, Path failedPath) {
		super();
		this.propsSet = propsSet;
		this.appHomePath = appHomePath;
		this.tmpPath = tmpPath;
		this.failedPath = failedPath;
	}

	/**
	 * Path からメールを１通復元し、処理します
	 */
	@Override
	public void run() {
		accept(mailPath);
	}

}
