package com.aquabasilea.alerting.sms.clicksend;

import com.aquabasilea.alerting.api.AlertResponse;
import com.aquabasilea.alerting.api.AlertSendException;
import com.aquabasilea.alerting.api.AlertSendService;
import com.aquabasilea.alerting.config.AlertSendConfig;
import com.aquabasilea.alerting.send.AlertSendInfos;
import com.aquabasilea.alerting.sms.clicksend.data.ClickSendAlertResponse;
import com.aquabasilea.alerting.sms.clicksend.data.ClickSendMessages;
import com.aquabasilea.alerting.sms.clicksend.data.ClickSendSms;
import com.aquabasilea.alerting.util.AuthenticationUtil;
import com.aquabasilea.util.JsonUtil;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Sms alert API From MessageBird
 */
public class ClickSendAlertSendServiceImpl implements AlertSendService {

   private static final Logger LOG = LoggerFactory.getLogger(ClickSendAlertSendServiceImpl.class);
   public static final String CLICKSEND_V3_BASE_REST_URL = "https://rest.clicksend.com/v3/";
   public static final String CLICKSEND_V3_SMS_SEND_URL = CLICKSEND_V3_BASE_REST_URL + "sms/send";
   private final String clickSendV3SmsSendUrl;
   private final Client client;

   public ClickSendAlertSendServiceImpl() {
      this(CLICKSEND_V3_SMS_SEND_URL);
   }

   public ClickSendAlertSendServiceImpl(String clickSendV3SmsSendUrl) {
      this.clickSendV3SmsSendUrl = clickSendV3SmsSendUrl;
      this.client = ClientBuilder.newClient();
   }

   @Override
   public AlertResponse sendAlert(AlertSendConfig alertSendConfig, AlertSendInfos alertSendInfos) throws AlertSendException {
      LOG.info("Sending text {} to {} receivers", alertSendInfos, alertSendInfos.receivers().size());
      ClickSendMessages clickSendMessages = createClickSendMessages(alertSendConfig, alertSendInfos);
      Entity<String> payload = Entity.json(JsonUtil.createJsonFromObject(clickSendMessages));

      try {
         return setSmsWitPayload(alertSendConfig, payload);
      } catch (Exception e) {
         throw new AlertSendException(e);
      }
   }

   private AlertResponse setSmsWitPayload(AlertSendConfig alertSendConfig, Entity<String> payload) {
      Response response = createAndSendRestRequest(alertSendConfig, payload, client);
      LOG.info("Sending text done, response [{}]", response);
      return ClickSendAlertResponse.of(response);
   }

   private Response createAndSendRestRequest(AlertSendConfig alertSendConfig, Entity<String> payload, Client client) {
      return client.target(clickSendV3SmsSendUrl)
              .request(MediaType.APPLICATION_JSON_TYPE)
              .header("Authorization", "Basic " + AuthenticationUtil.getEncodedUsernameAndPassword(alertSendConfig))
              .post(payload);
   }

   private static ClickSendMessages createClickSendMessages(AlertSendConfig alertSendConfig, AlertSendInfos alertSendInfos) {
      ClickSendMessages clickSendMessages = new ClickSendMessages();
      for (String receiver : alertSendInfos.receivers()) {
         ClickSendSms clickSendSms = createClickSendSms(alertSendConfig, alertSendInfos.msg(), receiver);
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
