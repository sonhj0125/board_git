package com.spring.app.common;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MySMTPAuthenticator extends Authenticator {
	
	@Override
	public PasswordAuthentication getPasswordAuthentication() {

		// Gmail 의 경우 @gmail.com 을 제외한 아이디만 입력한다.
		return new PasswordAuthentication("hjexample0125", "cnop tljo hucq jgtm");
		// "zivk wipx zpyr pami" 은 Google에 로그인 하기 위한 앱 비밀번호 이다.
	}
}
