package com.munepom.mailapp.mime.functions;


import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.mail.internet.MimePart;

/**
 *
 * MimePart ユーティリティ
 * charset を取得します
 * @author nishimura
 *
 */
public class GetCharSet implements Function<MimePart, String> {

	@Override
	public String apply(MimePart part) {
		GetContentType func = new GetContentType();
		String contentType = func.apply(part);
		if (Objects.isNull(contentType)) {
			return null;
		}

		return Optional.ofNullable( contentType.split("charset=") )
				.filter(arr -> arr.length > 1)
				.map(arr -> arr[1])
				.map(s -> s.replaceAll("\"", ""))
				.orElse(null);
	}
}
