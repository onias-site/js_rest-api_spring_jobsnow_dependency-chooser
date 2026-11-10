package com.js.rest.api.endpoints;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.json.fields.validation.JnJsonCommonsFields;
import com.js.rest.open.api.JsOpenApiPrivacyPolicy;
import com.js.services.JsServicePrivacyPolicy;

/** REST controller of the privacy policy at path {@code /privacy-policy}. Public: no session is required. */
@RestController
@RequestMapping("/privacy-policy")
public class JsRestApiPrivacyPolicy implements JsOpenApiPrivacyPolicy {

	/**
	 * The privacy policy in the language asked. Delegates to the matching service; the language comes from the query.
	 * @param language the {@code language} query parameter
	 * @return the response body
	 */
	@GetMapping
	public Map<String, Object> getPrivacyPolicy(@RequestParam("language") String language) {
		CcpJsonRepresentation json = CcpOtherConstants.EMPTY_JSON.put(JnJsonCommonsFields.language, language);
		Map<String, Object> result = JsServicePrivacyPolicy.GetPrivacyPolicy.execute(json.content);
		return result;
	}
}
