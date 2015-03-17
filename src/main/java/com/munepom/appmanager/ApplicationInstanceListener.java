package com.munepom.appmanager;

/**
 *
 * アプリケーション起動／停止用リスナー
 *
 * @author munepom
 *
 */
public interface ApplicationInstanceListener {
	public void newInstanceCreated() throws Exception;

	public void stop() throws Exception;
}
