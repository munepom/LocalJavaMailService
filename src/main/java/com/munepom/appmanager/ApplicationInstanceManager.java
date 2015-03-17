package com.munepom.appmanager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munepom.appmanager.functions.GetClientSocket;
import com.munepom.appmanager.functions.GetLocalInetAddress;
import com.munepom.appmanager.functions.GetServerSocket;

/**
 *
 * ポートバインディングを利用して、アプリケーション起動制御を行います。
 *
 * 実行は #com.munepom.appmanager.ApplicationInstanceRunnable で行います。
 *
 * シングルトン管理とします。
 *
 * @author munepom
 *
 */
public class ApplicationInstanceManager implements ApplicationInstanceProps {

	private static Logger log = LoggerFactory.getLogger( ApplicationInstanceManager.class );

	/**
	 * アプリケーション制御用 ServerSocket を作成します。 (Singleton)
	 * Registers this instance of the application.
	 *
	 * @return true if first instance, false if not.
	 */
	public static synchronized boolean registerInstance(int port, String threadName, ApplicationInstanceListener... listenerArray) throws Exception {
		// returnValueOnError should be true if lenient (allows app to run on network error) or false if strict.
		boolean isFirstInstance = false;

		// try to open network socket
		// if success, listen to socket for new instance message, return true
		// if unable to open, connect to existing and send new instance message, return false
		//ServerSocket の close は、ApplicationInstanceRunnable 側で行う
		InetAddress localInetAddress = new GetLocalInetAddress().get();
		ServerSocket socket = new GetServerSocket().apply(port, 10, localInetAddress);
		if( socket != null ) {
			isFirstInstance = true;

			// アプリケーション制御 Runnable 生成
			ApplicationInstanceRunnable runnable = new ApplicationInstanceRunnable(socket);
			runnable.setSubListenerArray(listenerArray);

			ExecutorService service = Executors.newFixedThreadPool(1,  r -> {
				// daemon 化しておくと、メインスレッド終了後にシャットダウンしてくれる
				Thread t = new Thread(r, threadName);
				t.setDaemon(false);
				return t;
			});
			try {
				service.execute(runnable);
			}
			finally {
			//shutdown をお忘れなく。すぐに停止するわけではなく、現在のタスクが完了後にシャットダウンしてくれる。
				service.shutdown();
			}
		}

		return isFirstInstance;
	}

	/**
	 * 起動／停止キーをポートへ送信します
	 * @param port
	 * @param key
	 * @param isFirstInstance
	 */
	public static synchronized void sendKey(int port, String key,  boolean isFirstInstance) {
		log.info("Key : {}", key);
		boolean isStartKey = SingleInstanceSharedKey.Start.getTrimmedKey().equals(key);
		if ( ! isFirstInstance && isStartKey ) {
			log.warn("Service has already launched.");
			return;
		}

		try ( Socket clientSocket = new GetClientSocket().apply(new GetLocalInetAddress().get(), port) ) {
			try ( OutputStream out = clientSocket.getOutputStream() ) {
				out.write(key.getBytes());
			}
			catch (IOException e) {
				log.error("Error sending key.");
			}
		}
		catch (IOException e) {
			log.error("Error connecting to local port for single instance notification.");
		}

	}

}
