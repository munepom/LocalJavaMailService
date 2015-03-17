package com.munepom.mailapp.mime.functions;


import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.mail.internet.MimePart;

/**
 *
 * メールヘッダから Content-Type を取得します
 * @author nishimura
 *
 */
public class GetContentType implements Function<MimePart, String> {

	@Override
	public String apply(MimePart part) {
		if (Objects.isNull(part)) {
			return null;
		}

		String[] headers = null;
		try {
			headers = part.getHeader("Content-Type");
		} catch (MessagingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		if (Objects.isNull(headers)) {
			return null;
		}

		return Stream.of(headers).findFirst().orElse("text/plain; charset=iso-2022-jp");
	}
}
