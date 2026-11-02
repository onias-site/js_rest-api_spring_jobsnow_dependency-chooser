package com.js.rest.open.api;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * OpenAPI contract of the webhook of Telegram at path {@code /telegram-webhook/{botName}}. Only Telegram calls it: it has
 * no session, and the request is authenticated by the secret given to Telegram when the webhook was registered.
 */
@RequestMapping("/telegram-webhook/{botName}")
@Tag(name = "Telegram webhook", description = "The updates Telegram pushes to the bots in production. Locally the bots read them by polling.")
public interface JsOpenApiTelegramWebhook {

	/**
	 * Receives an update.
	 * @param botName the bot
	 * @param webhookSecret the secret in the header of the request
	 * @param update the update
	 * @return an empty body
	 */
	@Operation(
		summary = "Receive an update of a bot",
		description = "When does it occur? When a user sends a message to the bot on Telegram, once the webhook was registered. "
			+ "What does it do? Checks the secret, saves the offset of the bot and runs the message in the bot, inside this request. "
			+ "A delivery repeated by Telegram does nothing, and an update without a message only moves the offset. "
			+ "No session is required."
			+ "<br/><br/><b>Path variables:</b><ul>"
			+ "<li><b>botName</b> – Required. The bot: support or user.</li>"
			+ "</ul>"
			+ "<b>Headers:</b><ul>"
			+ "<li><b>X-Telegram-Bot-Api-Secret-Token</b> – Required. The secret given to Telegram when the webhook was registered.</li>"
			+ "</ul>"
			+ "<b>Request body fields (JSON):</b><ul>"
			+ "<li><b>update_id</b> – Required. The id of the update.</li>"
			+ "<li><b>message</b> – Optional. The message, as Telegram sends it.</li>"
			+ "</ul>"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Update handled, or a repeated delivery ignored. Telegram will not send it again."),
		@ApiResponse(responseCode = "401", description = "The secret is missing or wrong: the request did not come from Telegram."),
		@ApiResponse(responseCode = "422", description = "Validation error: an unknown bot, or an update without update_id."),
		@ApiResponse(responseCode = "500", description = "The bot failed. The error was recorded and the offset already moved, so the delivery that Telegram repeats does nothing."),
	})
	@PostMapping
	Map<String, Object> receiveUpdate(@PathVariable("botName") String botName,
			@RequestHeader(name = "X-Telegram-Bot-Api-Secret-Token", defaultValue = "") String webhookSecret,
			@RequestBody Map<String, Object> update);
}
