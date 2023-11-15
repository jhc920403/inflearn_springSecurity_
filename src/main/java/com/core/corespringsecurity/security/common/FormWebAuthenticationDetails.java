package com.core.corespringsecurity.security.common;

import lombok.Getter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * 사용자가 전달하는 인증 정보와 파라미터 정보를 지속적으로 사용할 수 있도록 제공하는 기능을 지원한다.
 */
@Getter
public class FormWebAuthenticationDetails extends WebAuthenticationDetails {

    private String securityKey;

    public FormWebAuthenticationDetails(HttpServletRequest request) {
        super(request);

        securityKey = request.getParameter("secret_key");
    }
}
