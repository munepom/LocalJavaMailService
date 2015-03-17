package com.munepom.mailapp;

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.mail.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munepom.appmanager.ApplicationInstanceListener;

/**
 *
 * メール読み出し or メール処理 実行
 * @author nishimura
 *
 */
public abstract class AbstractMailAppRunner implements Runnable, ApplicationInstanceListener {

	protected Logger log = LoggerFactory.getLogger( this.getClass() );

	protected boolean isRunnable = true;

	protected String threadName = "";
	protected boolean daemon = false;
//	protected Path appHomePath;
//	protected Path tmpPath;
//	protected Path failedPath;
	protected PropsSetMailServer propsSet;
	protected boolean isParallel = false;

	protected Address[] errorFrom;

	protected BlockingQueue<Path> queue = new LinkedBlockingQueue<>();

	public AbstractMailAppRunner(){
		super();
	}

	public AbstractMailAppRunner(String threadName, boolean daemon, BlockingQueue<Path> queue, PropsSetMailServer propsSet, Address[] errorFrom, boolean isParallel) {
		super();
		this.threadName = threadName;
		this.daemon = daemon;
//		this.appHomePath = appHomePath;
//		this.tmpPath = tmpPath;
//		this.failedPath = failedPath;
		this.queue = queue;
		this.propsSet = propsSet;
		this.errorFrom = errorFrom;
		this.isParallel = isParallel;
	}

}
