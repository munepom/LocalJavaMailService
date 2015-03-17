package com.munepom.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface FileUtils {

	Logger log = LoggerFactory.getLogger( FileUtils.class );

	default boolean copyFile(Path pathSrc, Path pathDest) {
		boolean result = false;

		try (
			FileInputStream fis = new FileInputStream(pathSrc.toFile());
			FileOutputStream fos = new FileOutputStream(pathDest.toFile());
			FileChannel src  = fis.getChannel();
			FileChannel dest = fos.getChannel();
		)
		{
			//renameTo メソッドは、NFS に対応できない。
//			srcFile.renameTo(destFile);

			//これは非推奨。
//			src.transferTo(0, src.size(), dest);
			// 大容量ファイルの場合、以下のようにしないと不完全コピーになってしまうことがあるようだ。
			long pos = 0;
			long size = src.size();
			long add = 0;
			while( pos < size ) {
				add = src.transferTo(pos, size, dest);
				if( add == 0 ) {
					throw new IOException();
				}
				pos += add;
			}

			result = true;
		}
		catch (Exception e){
			log.error("ERROR", e);
			result = false;
		}

		return result;
	}

}
