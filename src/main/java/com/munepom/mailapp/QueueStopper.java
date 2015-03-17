package com.munepom.mailapp;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

public class QueueStopper implements Path {

	@Override
	public FileSystem getFileSystem() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public boolean isAbsolute() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public Path getRoot() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Path getFileName() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Path getParent() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public int getNameCount() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public Path getName(int index) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Path subpath(int beginIndex, int endIndex) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public boolean startsWith(Path other) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean startsWith(String other) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean endsWith(Path other) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean endsWith(String other) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public Path normalize() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Path resolve(Path other) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Path resolve(String other) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Path resolveSibling(Path other) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Path resolveSibling(String other) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Path relativize(Path other) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public URI toUri() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Path toAbsolutePath() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Path toRealPath(LinkOption... options) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public File toFile() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public WatchKey register(WatchService watcher, Kind<?>[] events,
			Modifier... modifiers) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public WatchKey register(WatchService watcher, Kind<?>... events)
			throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Iterator<Path> iterator() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public int compareTo(Path other) {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

}
