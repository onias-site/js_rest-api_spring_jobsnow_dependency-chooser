package com.js.rest.api.endpoints;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.ccp.aop.CcpAllowNullParameter;
import com.ccp.aop.CcpAllowNullReturn;
import com.ccp.decorators.CcpJsonFieldName;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Puts a {@code Cache-Control} in the successful (200) responses of {@link JsRestApiPrivacyPolicy}, the only route of the js
 * behind the CDN: the text changes only when {@code documentation/deploy/20-politica-de-privacidade.py} writes it again,
 * which also invalidates the CDN. 5 minutes in the browser, 1 year (the limit of Cloud CDN) in the CDN. An error comes from
 * the exception handler, another class, so it never gets the header.
 */
@RestControllerAdvice(assignableTypes = JsRestApiPrivacyPolicy.class)
public class JsRestApiPrivacyPolicyCacheHeader implements ResponseBodyAdvice<Object> {

	enum Fields implements CcpJsonFieldName {
		CACHE_CONTROL {
			public String getValue() {
				return "public, max-age=300, s-maxage=31536000";
			}
		},
	}

	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		Class<?> containingClass = returnType.getContainingClass();
		boolean privacyPolicyRoute = JsRestApiPrivacyPolicy.class.equals(containingClass);
		return privacyPolicyRoute;
	}

	@CcpAllowNullParameter
	@CcpAllowNullReturn
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

		boolean notAServletResponse = false == response instanceof ServletServerHttpResponse;
		if(notAServletResponse) {
			return body;
		}

		ServletServerHttpResponse servletServerHttpResponse = (ServletServerHttpResponse) response;
		HttpServletResponse servletResponse = servletServerHttpResponse.getServletResponse();
		int status = servletResponse.getStatus();
		int ok = HttpStatus.OK.value();
		boolean notOk = status != ok;
		if(notOk) {
			return body;
		}

		String headerValue = Fields.CACHE_CONTROL.getValue();
		HttpHeaders headers = response.getHeaders();
		headers.set(HttpHeaders.CACHE_CONTROL, headerValue);
		return body;
	}
}
