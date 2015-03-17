package com.munepom.appmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * アプリケーション制御実行クラス
 *
 * ポートバインディングを利用して、アプリケーションを起動／停止します。
 *
 * @author munepom
 *
 */
public class ApplicationInstanceRunnable implements Runnable, ApplicationInstanceProps {

	private Logger log = LoggerFactory.getLogger(getClass());

	private ApplicationInstanceListener[] subListenerArray;

	private ServerSocket socket = null;

	/** default constructor*/
	public ApplicationInstanceRunnable(){
	}

	public ApplicationInstanceRunnable(ServerSocket socket) {
		super();
		this.socket = socket;
	}

	public void setSubListenerArray(ApplicationInstanceListener[] subListenerArray) {
		this.subListenerArray = subListenerArray;
	}

	/**
	 * run server
	 */
	@Override
	public void run() {
		if( this.socket == null ) {
			log.warn("ServerSocket is null...");
			return;
		}

		boolean isStop = false;
		Socket client = null;
		try {
			while (!isStop) {
				try {
					if (this.socket.isClosed()) {
						isStop = true;
					}
					else {
						// accpet を利用して、Stop メッセージが来るまで待機し続ける
						client = this.socket.accept();

						// メッセージが来たら、処理開始
						try( BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream())) ) {
							String message = in.readLine();
							log.info("Message : {}", message);
							if( SingleInstanceSharedKey.Start.getTrimmedKey().equals(message) ) {
								log.info("Start new application instance.");
								fireNewInstance();
							}
							else if( SingleInstanceSharedKey.Stop.getTrimmedKey().equals(message) ) {
								log.info("Stop application instance.");
								stop();
							}
							else {
								// do nothing
								log.info("Other keys");
							}
						}
						catch (Exception e) {
							// 例外発生時は、強制停止
							log.warn("アプリケーションインスタンスの起動／停止処理実行時に例外発生");
							log.error(e.getMessage(), e);
							stop();
						}
					}
				}
				catch (IOException e) {
					log.error(e.getMessage(), e);
					isStop = true;
				}
			}
		}
		catch(Exception e) {
			log.warn("アプリケーションインスタンスの起動／停止処理実行時に予期せぬ例外発生");
			log.error(e.getMessage(), e);
		}
		finally {
			try {
				if( this.socket != null ) {
					this.socket.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private void fireNewInstance() throws Exception {
		// Stream を使うと、Lambda 式内の例外を外に投げられない？ので、普通に処理。
		if( Objects.nonNull(subListenerArray) ) {
			for(ApplicationInstanceListener listener : subListenerArray) {
				listener.newInstanceCreated();
			}
		}
	}

	private void stop() throws Exception {
		try {
			this.socket.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		if( Objects.nonNull(subListenerArray) ) {
			for(ApplicationInstanceListener listener : subListenerArray) {
				listener.stop();
			}
		}
	}
}