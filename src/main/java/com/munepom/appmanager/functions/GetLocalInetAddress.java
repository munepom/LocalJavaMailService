package com.munepom.appmanager.functions;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * ローカル InetAddress を取得します
 *
 * @author munepom
 *
 */
public class GetLocalInetAddress implements Supplier<InetAddress> {

	Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public InetAddress get() {
		try {
			return InetAddress.getByAddress(new byte[]{127,0,0,1});
		} catch (UnknownHostException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
}
