package com.js.rest.api.telegram;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.implementations.http.apache.mime.CcpApacheMimeHttp;
import com.ccp.implementations.json.gson.CcpGsonJsonHandler;
import com.jn.json.fields.validation.JnJsonCommonsFields;
import com.jn.json.fields.validation.JnJsonInstantMessengerFields;

/**
 * Registers or deletes the webhook of a bot, to be run by hand: once at each new address of the API or new secret of the
 * bot, the {@code telegramWebhookSecrets} system property.
 * <p>Arguments:</p>
 * <ol>
 * <li>the operation, {@code register} or {@code delete}</li>
 * <li>the bot, {@code support} or {@code user}</li>
 * <li>for {@code register}, the public HTTPS address of the API, without the path, on port 443, 80, 88 or 8443</li>
 * </ol>
 * <p>While the webhook of a bot exists, {@code JbInstantMessengerReaderStarter} can not read it: delete the webhook first,
 * or run the polling with the token of another bot.</p>
 */
public class JsTelegramWebhookStarter {

	/** The position of the bot in the arguments. */
	private static final int BOT_POSITION = 1;

	/** The position of the address in the arguments. */
	private static final int ADDRESS_POSITION = 2;

	/** The arguments of the starter. */
	enum Fields implements CcpJsonFieldName {
		/** The address of a bot that needs none. */
		noAddress {
			public String getValue() {
				return "";
			}
		},
	}

	/**
	 * Runs the operation over the bot and prints the answer of Telegram.
	 * @param args the operation, the bot and the address of the API
	 */
	public static void main(String[] args) {
		CcpDependencyInjection.loadAllDependencies(CcpGsonJsonHandler.INSTANCE, CcpApacheMimeHttp.INSTANCE);

		String operationName = args[0];
		JsTelegramWebhookOperations operation = JsTelegramWebhookOperations.valueOf(operationName);
		boolean addressGiven = args.length > ADDRESS_POSITION;
		String apiAddress = addressGiven ? args[ADDRESS_POSITION] : Fields.noAddress.getValue();

		CcpJsonRepresentation request = CcpOtherConstants.EMPTY_JSON
				.put(JnJsonInstantMessengerFields.botName, args[BOT_POSITION])
				.put(JnJsonCommonsFields.url, apiAddress)
				;

		CcpBusiness business = operation.getBusiness();
		CcpJsonRepresentation answer = business.execute(request);
		System.out.println(answer);
	}
}
