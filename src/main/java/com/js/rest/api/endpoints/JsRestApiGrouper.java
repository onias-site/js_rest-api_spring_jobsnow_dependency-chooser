package com.js.rest.api.endpoints;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.business.grouper.JnBusinessGetGroup;
import com.jn.business.grouper.JnBusinessGetGroupOnBothSides;
import com.js.rest.open.api.JsOpenApiGrouper;
import com.js.services.JsServiceGrouper;

/** Read of the groups of {@code jn_grouper} by their primary key, for the logged-in user. */
@CrossOrigin
@RestController
@RequestMapping("/grouper/{email}")
public class JsRestApiGrouper implements JsOpenApiGrouper {

	/**
	 * A group by its primary key. Delegates to the matching service; the keys and the side come from the path and the
	 * query, which the filters do not bring into the body.
	 * @param sessionValues the request body with the values of the session
	 * @param parentName the {@code parentName} path variable
	 * @param childName the {@code childName} path variable
	 * @param parentId the {@code parentId} path variable
	 * @param side the {@code side} query parameter ({@code mainEntity} or {@code twinEntity})
	 * @return the response body
	 */
	@GetMapping("/{parentName}/{childName}/{parentId}")
	public Map<String, Object> getGroup(@RequestBody String sessionValues, @PathVariable("parentName") String parentName, @PathVariable("childName") String childName,
			@PathVariable("parentId") String parentId, @RequestParam(value = "side", defaultValue = "mainEntity") String side) {
		CcpJsonRepresentation session = new CcpJsonRepresentation(sessionValues);
		CcpJsonRepresentation sessionWithParentName = session.put(JnBusinessGetGroup.Fields.parentName, parentName);
		CcpJsonRepresentation sessionWithChildName = sessionWithParentName.put(JnBusinessGetGroup.Fields.childName, childName);
		CcpJsonRepresentation sessionWithParentId = sessionWithChildName.put(JnBusinessGetGroup.Fields.parentId, parentId);
		CcpJsonRepresentation json = sessionWithParentId.put(JnBusinessGetGroup.Fields.side, side);
		Map<String, Object> result = JsServiceGrouper.GetGroup.execute(json.content);
		return result;
	}

	/**
	 * A group by its primary key on both sides at once. Delegates to the matching service; the keys come from the path,
	 * which the filters do not bring into the body.
	 * @param sessionValues the request body with the values of the session
	 * @param parentName the {@code parentName} path variable
	 * @param childName the {@code childName} path variable
	 * @param parentId the {@code parentId} path variable
	 * @return the response body
	 */
	@GetMapping("/{parentName}/{childName}/{parentId}/bothSides")
	public Map<String, Object> getGroupOnBothSides(@RequestBody String sessionValues, @PathVariable("parentName") String parentName, @PathVariable("childName") String childName,
			@PathVariable("parentId") String parentId) {
		CcpJsonRepresentation session = new CcpJsonRepresentation(sessionValues);
		CcpJsonRepresentation sessionWithParentName = session.put(JnBusinessGetGroupOnBothSides.Fields.parentName, parentName);
		CcpJsonRepresentation sessionWithChildName = sessionWithParentName.put(JnBusinessGetGroupOnBothSides.Fields.childName, childName);
		CcpJsonRepresentation json = sessionWithChildName.put(JnBusinessGetGroupOnBothSides.Fields.parentId, parentId);
		Map<String, Object> result = JsServiceGrouper.GetGroupOnBothSides.execute(json.content);
		return result;
	}
}
