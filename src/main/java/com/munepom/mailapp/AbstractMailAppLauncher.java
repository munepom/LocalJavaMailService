package com.munepom.mailapp;


import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.mail.Address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munepom.appmanager.ApplicationInstanceManager;
import com.munepom.appmanager.ApplicationInstanceProps;

/**
 *
 * メールアプリ起動用クラス
 *
 * @author nishimura
 *
 */
public abstract class AbstractMailAppLauncher implements ApplicationInstanceProps {

	private Logger log = LoggerFactory.getLogger( this.getClass() );

	/**
	 * メール読み出し用設定
	 */
	protected MailServerProps propsSet;

	/**
	 * エラーメッセージ from (javax.mail.Address は Serializable ではないので、固定値設定)
	 */
	protected Address[] errorFrom;

	/**
	 * アプリケーション制御ポート (シングルインスタンス制御)
	 */
	protected int port;

	/**
	 * スレッド名: アプリケーション起動、停止
	 */
	protected String threadNameAppManager;

	/**
	 * スレッド名: メールサーバ
	 */
	protected String threadNameMailServer;

	/**
	 * スレッド名: メール処理
	 */
	protected String threadNameMailConsumer;

	/**
	 * アプリケーションホームパス
	 */
	protected Path appHomePath;

	/**
	 * メール一時保存パス
	 */
	protected Path tmpPath;

	/**
	 * メール読み込み失敗時保存パス
	 */
	protected Path failedPath;

	/**
	 * メール読み出し実行クラス
	 */
	protected MailReader reader;

	/**
	 * メール処理実行クラス (スレッド起動させるため、ConsumerRunner で deep clone 必須)
	 */
	protected MailConsumer consumer;

	/**
	 * 並列読取りモード
	 */
	protected boolean isParallelRead = false;

	/**
	 * 並列処理モード
	 */
	protected boolean isParallelConsumer = false;

	/**
	 * メール処理スレッド数
	 */
	protected int consumerThreadNum = 1;

	/**
	 * メール蓄積 Queue
	 */
	private BlockingQueue<Path> queue = new LinkedBlockingQueue<>();

	/**
	 * メール読み取り間隔 (ミリ秒)
	 */
	protected long span = 10000L;

//	public boolean init() {
//		try {
//			Files.createDirectories(path);
//			Files.createDirectories(tmpPath);
//			Files.createDirectories(failedPath);
//		} catch (IOException e) {
//			log.error(e.getMessage(), e);
//			return false;
//		}
//
//		return true;
//	}

	/**
	 * メーリングリスト起動 or 停止
	 * @param args
	 */
	public void execute(String cmd) throws Exception {
//		boolean isPassedArgs = checkArgs(args);
//		boolean isPassedConfig = checkConfig();
//		if( ! isPassedArgs || ! isPassedConfig ) {
//			log.warn("コマンドまたは設定を見直してください");
//			return;
//		}
		if (Objects.isNull(queue)) {
			queue = new LinkedBlockingQueue<>();
		}

		//Runner instance (処理実行) 作成
		MailAppMailServerRunner runnerMailServer = new MailAppMailServerRunner(threadNameMailServer, false, queue, propsSet, errorFrom, isParallelRead, reader, span);
		MailAppMailConsumerRunner runnerMailHandler = new MailAppMailConsumerRunner(threadNameMailConsumer, true, queue, propsSet, errorFrom, isParallelConsumer, consumer, consumerThreadNum);
//		MailingListWatchRunner runnable = new MailingListWatchRunner(THREAD_NAME_RUNNER, path);

		//create ServerSocket & ExecutorService (サービス起動 or 停止)
		boolean isFirstInstance = ApplicationInstanceManager.registerInstance(port, threadNameAppManager, runnerMailServer, runnerMailHandler);

		//send cmd
		ApplicationInstanceManager.sendKey(port, cmd, isFirstInstance);
	}

}
