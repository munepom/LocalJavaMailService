package com.munepom.appmanager.functions;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * クライアント側の Socket を生成します。
 *
 * 生成された Socket を利用すれば、ローカルサーバへ key 送信が可能です。
 *
 * @author munepom
 *
 */
public class GetClientSocket implements BiFunction<InetAddress, Integer, Socket> {

	Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public Socket apply(InetAddress address, Integer port) {
		Socket socket = null;
		try {
			socket = new Socket(address, port);
		} catch (UnknownHostException e) {
			log.error(e.getMessage(), e);
			socket = null;
		} catch (IOException e1) {
			log.error("Error connecting to local port for single instance notification");
			log.error(e1.getMessage(), e1);
			socket = null;
		}
		return socket;
	}
}
