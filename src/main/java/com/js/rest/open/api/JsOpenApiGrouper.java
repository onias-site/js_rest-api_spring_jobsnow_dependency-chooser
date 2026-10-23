package com.js.rest.open.api;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * OpenAPI contract for the read of the groups of {@code jn_grouper} at path {@code /grouper/{email}}: a group by its
 * primary key, for the logged-in user (the session is validated as in the other APIs).
 */
@RequestMapping("/grouper/{email}")
@Tag(name = "Grouper", description = "Read of the groups of jn_grouper by their primary key: parent name, child name and parent id.")
public interface JsOpenApiGrouper {

	/**
	 * A group by its primary key.
	 * @param sessionValues the request body with the values of the session
	 * @param parentName the {@code parentName} path variable
	 * @param childName the {@code childName} path variable
	 * @param parentId the {@code parentId} path variable
	 * @param side the {@code side} query parameter ({@code mainEntity} or {@code twinEntity})
	 * @return the response body
	 */
	@Operation(
		summary = "A group of jn_grouper by its primary key",
		description = "When does it occur? When a screen needs a group already calculated by the writes of an entity (the views of the month "
			+ "of a recruiter, the views of the month of a resume, the resumes of a hash...). "
			+ "What does it do? Reads, by id with no query, the group on the side asked: the groups of the active records (jn_grouper, the "
			+ "default) or the ones of the inactive records (jn_inactive_grouper). Its children are ids, JSON texts or, in a group of "
			+ "counters, a single JSON text of counters; childCount is the size of the child list, but in a group of counters, where it "
			+ "is the number of keys."
			+ "<br/><br/><b>Path variables:</b><ul>"
			+ "<li><b>email</b> – Required. The logged-in user's e-mail.</li>"
			+ "<li><b>parentName</b> – Required. The kind of group: the name of the entity that calculates the parent id (2 to 50 characters).</li>"
			+ "<li><b>childName</b> – Required. The name of the entity of the children (2 to 50 characters).</li>"
			+ "<li><b>parentId</b> – Required. The id of the parent (1 to 100 characters).</li>"
			+ "</ul>"
			+ "<b>Query parameters:</b><ul>"
			+ "<li><b>side</b> – Optional. mainEntity (default), the groups of the active records, or twinEntity, the ones of the inactive records.</li>"
			+ "</ul>"
	)
	@ApiResponses({
		@ApiResponse(content = {
			@Content(schema = @Schema(example = "{"
				+ "\"parentId\": \"8f3c...\","
				+ "\"parentName\": \"vis_resume_views_of_the_month\","
				+ "\"childName\": \"vis_viewed_resume\","
				+ "\"parentJson\": {\"email\": \"5e2b...\", \"monthYear\": \"202610\"},"
				+ "\"child\": [\"{\\\"empresa.com.br\\\":2,\\\"consultoria.com\\\":1}\"],"
				+ "\"childCount\": 2"
				+ "}")) },
			responseCode = "200",
			description = "The group."),
		@ApiResponse(responseCode = "401", description = "No valid login."),
		@ApiResponse(responseCode = "404", description = "No group with this primary key on the side asked."),
		@ApiResponse(responseCode = "422", description = "Validation error — a key out of its size, or a side other than mainEntity and twinEntity."),
	})
	@GetMapping("/{parentName}/{childName}/{parentId}")
	Map<String, Object> getGroup(@RequestBody String sessionValues, @PathVariable("parentName") String parentName, @PathVariable("childName") String childName,
			@PathVariable("parentId") String parentId, @RequestParam(value = "side", defaultValue = "mainEntity") String side);

	/**
	 * A group by its primary key on both sides at once.
	 * @param sessionValues the request body with the values of the session
	 * @param parentName the {@code parentName} path variable
	 * @param childName the {@code childName} path variable
	 * @param parentId the {@code parentId} path variable
	 * @return the response body
	 */
	@Operation(
		summary = "A group of jn_grouper by its primary key, on both sides at once",
		description = "When does it occur? When a screen needs, in one request, the active and the inactive children of a group "
			+ "(the sessions of a user still open and the ones already closed, for one). "
			+ "What does it do? Reads, by id with no query and in a single search, the group on both sides: jn_grouper (the groups of "
			+ "the active records) and jn_inactive_grouper (the ones of the inactive records). Each side comes under the name of its "
			+ "entity, with an empty json when the group does not exist there."
			+ "<br/><br/><b>Path variables:</b><ul>"
			+ "<li><b>email</b> – Required. The logged-in user's e-mail.</li>"
			+ "<li><b>parentName</b> – Required. The kind of group: the name of the entity that calculates the parent id (2 to 50 characters).</li>"
			+ "<li><b>childName</b> – Required. The name of the entity of the children (2 to 50 characters).</li>"
			+ "<li><b>parentId</b> – Required. The id of the parent (1 to 100 characters).</li>"
			+ "</ul>"
	)
	@ApiResponses({
		@ApiResponse(content = {
			@Content(schema = @Schema(example = "{"
				+ "\"jn_grouper\": {\"parentId\": \"8f3c...\", \"parentName\": \"js_login_email\", \"childName\": \"js_login_session_validation\","
				+ " \"parentJson\": {\"email\": \"5e2b...\"}, \"child\": [\"a1b2...\"], \"childCount\": 1},"
				+ "\"jn_inactive_grouper\": {}"
				+ "}")) },
			responseCode = "200",
			description = "The group of each side."),
		@ApiResponse(responseCode = "401", description = "No valid login."),
		@ApiResponse(responseCode = "404", description = "No group with this primary key on either side."),
		@ApiResponse(responseCode = "422", description = "Validation error — a key out of its size."),
	})
	@GetMapping("/{parentName}/{childName}/{parentId}/bothSides")
	Map<String, Object> getGroupOnBothSides(@RequestBody String sessionValues, @PathVariable("parentName") String parentName, @PathVariable("childName") String childName,
			@PathVariable("parentId") String parentId);
}
