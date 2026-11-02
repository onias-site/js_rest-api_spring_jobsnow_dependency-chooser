package com.js.rest.api.endpoints;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ccp.decorators.CcpJsonRepresentation;
import com.js.business.telegram.JsBusinessValidateTelegramWebhookSecret;
import com.js.rest.open.api.JsOpenApiTelegramWebhook;
import com.js.services.JsServiceTelegramWebhook;
import com.jn.json.fields.validation.JnJsonInstantMessengerFields;

/**
 * REST controller of the webhook of Telegram at path {@code /telegram-webhook/{botName}}: the updates of the bots in
 * production. It is not {@code @CrossOrigin} because no browser calls it, and it has no filter of session or e-mail.
 */
@RestController
@RequestMapping("/telegram-webhook/{botName}")
public class JsRestApiTelegramWebhook implements JsOpenApiTelegramWebhook {

	/**
	 * Receives an update. Delegates to the matching service, with the bot and the secret, which do not come in the body.
	 * @param botName the bot
	 * @param webhookSecret the secret in the header of the request
	 * @param update the update
	 * @return an empty body
	 */
	@PostMapping
	public Map<String, Object> receiveUpdate(@PathVariable("botName") String botName,
			@RequestHeader(name = "X-Telegram-Bot-Api-Secret-Token", defaultValue = "") String webhookSecret,
			@RequestBody Map<String, Object> update) {
		CcpJsonRepresentation json = new CcpJsonRepresentation(update);
		CcpJsonRepresentation jsonWithBotName = json.put(JnJsonInstantMessengerFields.botName, botName);
		CcpJsonRepresentation jsonWithSecret = jsonWithBotName.put(JsBusinessValidateTelegramWebhookSecret.Fields.webhookSecret, webhookSecret);
		Map<String, Object> result = JsServiceTelegramWebhook.ReceiveTelegramUpdate.execute(jsonWithSecret.content);
		return result;
	}
}
