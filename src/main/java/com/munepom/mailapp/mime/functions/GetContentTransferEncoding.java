package com.munepom.mailapp.mime.functions;


import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.mail.internet.MimePart;

/**
 *
 * メールヘッダユーティリティ
 *
 * Content-Transfer-Encoding を取得します
 *
 * @author nishimura
 *
 */
public class GetContentTransferEncoding implements Function<MimePart, String> {

	@Override
	public String apply(MimePart part) {
		if (Objects.isNull(part)) {
			return null;
		}

		String[] headers = null;
		try {
			headers = part.getHeader("Content-Transfer-Encoding");
		} catch (MessagingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		if (Objects.isNull(headers)) {
			return null;
		}

		GetCharSet func = new GetCharSet();
		String charset = func.apply(part);

		return Stream.of(headers).findFirst().orElse(Objects.nonNull(charset) && charset.equalsIgnoreCase("iso-2022-jp") ? "7bit" : "");
	}
}
