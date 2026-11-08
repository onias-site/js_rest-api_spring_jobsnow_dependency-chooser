package com.js.rest.open.api;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/** OpenAPI contract of the privacy policy at path {@code /privacy-policy}. Public: no session is required. */
@RequestMapping("/privacy-policy")
@Tag(name = "Privacy policy", description = "The privacy policy of the platform, in the language asked.")
public interface JsOpenApiPrivacyPolicy {

	/**
	 * The privacy policy in the language asked.
	 * @param language the {@code language} query parameter
	 * @return the response body
	 */
	@Operation(
		summary = "The privacy policy",
		description = "When does it occur? When the privacy policy page opens (the footer of every e-mail links to it). "
			+ "What does it do? Reads, by id with no query, the text of the privacy policy in the language asked; a language with no "
			+ "text of its own gets the English one. The text has one block per line: a line starting with '# ' is the title of the page, "
			+ "with '## ' the title of a section, with '- ' an item of a list, and any other line a paragraph. No session is required."
			+ "<br/><br/><b>Query parameters:</b><ul>"
			+ "<li><b>language</b> – Required. One of portuguese, english, spanish.</li>"
			+ "</ul>"
	)
	@ApiResponses({
		@ApiResponse(content = {
			@Content(schema = @Schema(example = "{"
				+ "\"language\": \"english\","
				+ "\"message\": \"# Privacy policy\\n## 1. Who we are\\nOnias.Site (https://onias.site) is a platform...\""
				+ "}")) },
			responseCode = "200",
			description = "The privacy policy."),
		@ApiResponse(responseCode = "400", description = "The language parameter is missing."),
		@ApiResponse(responseCode = "422", description = "Validation error: a language other than portuguese, english and spanish."),
	})
	@GetMapping
	Map<String, Object> getPrivacyPolicy(@RequestParam("language") String language);
}
