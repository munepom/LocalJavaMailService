package com.munepom.appmanager;

/**
 *
 * Application の 起動／停止キー登録
 *
 * @author munepom
 *
 */
public interface ApplicationInstanceProps {

	/**
	 *
	 * SingleInstance で共有するキーを登録しておく
	 * @author munepom
	 *
	 */
	enum SingleInstanceSharedKey {
		/** Must end with newline */
		Start ("$$Start$$\n"),
		Stop  ("$$Stop$$\n"),
		;

		private String key;

		private SingleInstanceSharedKey(String key) {
			this.key = key;
		}

		public String getKey() {
			return this.key;
		}

		public String getTrimmedKey() {
			return this.key.trim();
		}
	}

}
