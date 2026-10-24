package com.js.rest.open.api;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * OpenAPI contract of the "contact us" at path {@code /contact-us/{email}}. It has no session: the login screen may be
 * the one with the bug, so the e-mail is the one the user is logged in with or the one the login modal keeps.
 */
@RequestMapping("/contact-us/{email}")
@Tag(name = "Contact us", description = "Tickets the user opens from the contact us modal: bugs, compliments, criticisms, suggestions, comments and questions.")
public interface JsOpenApiContactUs {

	/**
	 * Opens a ticket.
	 * @param body the request body
	 * @return the response body
	 */
	@Operation(
		summary = "Open a ticket of the contact us",
		description = "When does it occur? When the user sends the contact us modal. "
			+ "What does it do? Saves the ticket (one open ticket per e-mail and type: a new one of the same type takes the place of the open one), "
			+ "e-mails the user what they filled in, telling that the ticket is being analyzed, and sends the support the command to answer it. "
			+ "No session is required."
			+ "<br/><br/><b>Path variables:</b><ul>"
			+ "<li><b>email</b> – Required. The e-mail the user is logged in with, or the one the login modal keeps. Min 7, max 100 characters.</li>"
			+ "</ul>"
			+ "<b>Request body fields (JSON):</b><ul>"
			+ "<li><b>type</b> – Required. One of reportBug, makeCompliment, makeCriticism, makeSuggestion, makeComment, clarifyDoubts.</li>"
			+ "<li><b>title</b> – Required. 5 to 50 characters.</li>"
			+ "<li><b>description</b> – Required. 10 to 500 characters.</li>"
			+ "<li><b>stackTrace</b> – Optional. The last stack trace caught by the page, up to 50,000 characters.</li>"
			+ "</ul>"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Ticket saved. The frontend should tell the user that the support will answer by e-mail."),
		@ApiResponse(responseCode = "400", description = "Invalid email format."),
		@ApiResponse(responseCode = "403", description = "The support chose to ignore this user. The frontend should tell the user that the ticket could not be opened."),
		@ApiResponse(responseCode = "422", description = "Validation error: a field missing or out of its size, or an unknown type."),
		@ApiResponse(responseCode = "429", description = "Daily limit reached: a ticket of the same quota was opened in the last 24 hours (a question has its own quota; "
			+ "the other kinds, except bugs, share it with the skill suggestion and the skill hierarchy fix). The frontend should ask the user to try again tomorrow."),
	})
	@PostMapping
	Map<String, Object> saveContactUs(@RequestBody Map<String, Object> body);
}
