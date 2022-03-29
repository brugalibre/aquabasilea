package com.aquabasilea.alerting.sms.clicksend;

import com.aquabasilea.alerting.api.AlertSendException;
import com.aquabasilea.alerting.sms.clicksend.data.ClickSendMessages;
import com.aquabasilea.alerting.sms.clicksend.data.ClickSendSms;
import com.aquabasilea.alerting.config.AlertSendConfig;
import com.aquabasilea.alerting.api.AlertSendService;
import com.aquabasilea.alerting.util.AuthenticationUtil;
import com.aquabasilea.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Sms alert API From MessageBird
 */
public class ClickSendAlertSendServiceImpl implements AlertSendService {

   private static final Logger LOG = LoggerFactory.getLogger(ClickSendAlertSendServiceImpl.class);
   public static final String CLICKSEND_V3_BASE_REST_URL = "https://rest.clicksend.com/v3/";
   public static final String CLICKSEND_V3_SMS_SEND_URL = CLICKSEND_V3_BASE_REST_URL + "sms/send";
   private String clickSendV3SmsSendUrl;

   public ClickSendAlertSendServiceImpl(String clickSendV3SmsSendUrl) {
      this.clickSendV3SmsSendUrl = clickSendV3SmsSendUrl;
   }

   public ClickSendAlertSendServiceImpl() {
      this.clickSendV3SmsSendUrl = CLICKSEND_V3_SMS_SEND_URL;
   }

   @Override
   public String sendAlert(AlertSendConfig alertSendConfig, String msg) throws AlertSendException {
      LOG.info("Sending text {} to {} receivers", msg, alertSendConfig.getReceivers().size());
      ClickSendMessages clickSendMessages = createClickSendMessages(alertSendConfig, msg);
      Entity<String> payload = Entity.json(JsonUtil.createJsonFromObject(clickSendMessages));

      try {
         return setSmsWitPayload(alertSendConfig, payload);
      } catch (Exception e) {
         throw new AlertSendException(e);
      }
   }

   private String setSmsWitPayload(AlertSendConfig alertSendConfig, Entity<String> payload) {
      Client client = ClientBuilder.newClient();
      Response response = createAndSendRestRequest(alertSendConfig, payload, client);
      LOG.info("Sending text done, response '{}'", response);
      return response.toString();
   }

   private Response createAndSendRestRequest(AlertSendConfig alertSendConfig, Entity<String> payload, Client client) {
      return client.target(clickSendV3SmsSendUrl)
              .request(MediaType.APPLICATION_JSON_TYPE)
              .header("Authorization", "Basic " + AuthenticationUtil.getEncodedUsernameAndPassword(alertSendConfig))
              .post(payload);
   }

   private static ClickSendMessages createClickSendMessages(AlertSendConfig alertSendConfig, String msg) {
      ClickSendMessages clickSendMessages = new ClickSendMessages();
      for (String receiver : alertSendConfig.getReceivers()) {
         ClickSendSms clickSendSms = createClickSendSms(alertSendConfig, msg, receiver);
         clickSendMessages.getMessages().add(clickSendSms);
      }
      return clickSendMessages;
   }

   private static ClickSendSms createClickSendSms(AlertSendConfig alertSendConfig, String msg, String receiver) {
      ClickSendSms clickSendSms = new ClickSendSms();
      clickSendSms.setBody(msg);
      clickSendSms.setFrom(alertSendConfig.getOriginator());
      clickSendSms.setTo(receiver);
      clickSendSms.setSource(alertSendConfig.getOriginatorName());
      return clickSendSms;
   }
}
