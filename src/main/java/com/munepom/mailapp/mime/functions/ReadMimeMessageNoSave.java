package com.munepom.mailapp.mime.functions;


import java.util.function.Function;

import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.munepom.mailapp.dataset.MimeData;

/**
 *
 * メールを１通読み取り、DataSetMime オブジェクトに格納します。
 * 添付ファイル書き出しは行いません。
 *
 * @author nishimura
 *
 */
public class ReadMimeMessageNoSave implements Function<MimeMessage, MimeData>{

	Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public MimeData apply(MimeMessage mime) {
		ReadMimeMessage func = new ReadMimeMessage();
		return func.apply(mime, null);
	}


}
