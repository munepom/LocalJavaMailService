package com.munepom.mailapp.dataset;

import java.nio.file.Path;

import javax.mail.Message;

public class DataSetMessageCopiedPath {
	protected Message message;
	protected Path copiedPath;

	public DataSetMessageCopiedPath(){}

	public DataSetMessageCopiedPath(Message message, Path copiedPath) {
		this.message = message;
		this.copiedPath = copiedPath;
	}

	public Message getMessage() {
		return message;
	}

	public Path getCopiedPath() {
		return copiedPath;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public void setCopiedPath(Path copiedPath) {
		this.copiedPath = copiedPath;
	}
}
