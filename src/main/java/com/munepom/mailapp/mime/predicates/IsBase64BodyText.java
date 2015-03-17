package com.munepom.mailapp.mime.predicates;


import java.util.Objects;
import java.util.function.Predicate;

import javax.mail.internet.MimePart;

import com.munepom.mailapp.mime.functions.GetContentTransferEncoding;
import com.munepom.mailapp.mime.functions.GetContentType;

/**
 *
 * MimePart ユーティリティ
 *
 * Body が base64 エンコーディングの text かどうか判定します
 *
 * @author nishimura
 *
 */
public class IsBase64BodyText implements Predicate<MimePart> {

	@Override
	public boolean test(MimePart part) {
		GetContentTransferEncoding funcCTE = new GetContentTransferEncoding();
		String ctf = funcCTE.apply(part);
		if (Objects.isNull(ctf)) {
			return false;
		}

		GetContentType funcContent = new GetContentType();
		String contentType = funcContent.apply(part);
		if (Objects.isNull(contentType)) {
			return false;
		}

		return contentType.contains("text") && ctf.equalsIgnoreCase("base64");
	}
}
