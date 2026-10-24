package com.js.rest.api.endpoints;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.js.rest.open.api.JsOpenApiContactUs;
import com.js.services.JsServiceContactUs;

/**
 * REST controller of the "contact us" at path {@code /contact-us/{email}}: the tickets the user opens from the modal of
 * the front end. No session is required.
 */
@CrossOrigin
@RestController
@RequestMapping("/contact-us/{email}")
public class JsRestApiContactUs implements JsOpenApiContactUs {

	/**
	 * Opens a ticket. Delegates to the matching service; the e-mail of the path comes in the body, put by the filter.
	 * @param body the request body
	 * @return the response body
	 */
	@PostMapping
	public Map<String, Object> saveContactUs(@RequestBody Map<String, Object> body) {
		Map<String, Object> execute = JsServiceContactUs.SaveContactUs.execute(body);
		return execute;
	}
}
