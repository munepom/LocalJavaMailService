package com.munepom.appmanager.functions;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * サーバソケットを新規生成します。
 * 既に生成されている場合は、null を返します。
 *
 * @author munepom
 *
 */
public class GetServerSocket implements TriFunction<Integer, Integer, InetAddress, ServerSocket> {

	Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public ServerSocket apply(Integer port, Integer backlog, InetAddress bindAddr) {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(port, backlog, bindAddr);
			log.info("Listening for application instances on socket {}", port);
		} catch (UnknownHostException e) {
			log.error(e.getMessage(), e);
			socket = null;
		} catch (IOException e) {
			log.warn("Port is already used.");
			socket = null;
		}

		return socket;
	}

}
