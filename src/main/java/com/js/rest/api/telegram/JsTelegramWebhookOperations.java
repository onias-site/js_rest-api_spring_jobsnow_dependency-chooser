package com.js.rest.api.telegram;

import com.ccp.business.CcpBusiness;
import com.js.business.telegram.JsBusinessDeleteTelegramWebhook;
import com.js.business.telegram.JsBusinessRegisterTelegramWebhook;

/** What {@link JsTelegramWebhookStarter} can do with the webhook of a bot. */
enum JsTelegramWebhookOperations {
	/** Telegram starts to push the updates of the bot to the API. */
	register {
		/**
		 * Registers the webhook.
		 * @return the business
		 */
		CcpBusiness getBusiness() {
			return JsBusinessRegisterTelegramWebhook.INSTANCE;
		}
	},
	/** Telegram stops pushing the updates, and the bot goes back to being read by polling. */
	delete {
		/**
		 * Deletes the webhook.
		 * @return the business
		 */
		CcpBusiness getBusiness() {
			return JsBusinessDeleteTelegramWebhook.INSTANCE;
		}
	},
	;

	/**
	 * Returns the business of the operation.
	 * @return the business
	 */
	abstract CcpBusiness getBusiness();
}
