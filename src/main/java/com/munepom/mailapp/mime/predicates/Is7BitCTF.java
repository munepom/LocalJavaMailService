package com.munepom.mailapp.mime.predicates;


import java.util.Objects;
import java.util.function.Predicate;

import javax.mail.internet.MimePart;

import com.munepom.mailapp.mime.functions.GetContentTransferEncoding;

/**
 *
 * MimePart ユーティリティ
 *
 * Content-Transfer-Encoding が 7bit なら、true
 *
 * @author nishimura
 *
 */
public class Is7BitCTF implements Predicate<MimePart> {

	@Override
	public boolean test(MimePart part) {
		GetContentTransferEncoding funcCTE = new GetContentTransferEncoding();
		String ctf = funcCTE.apply(part);
		if (Objects.isNull(ctf)) {
			return false;
		}

		return ctf.equalsIgnoreCase("7bit");
	}

}