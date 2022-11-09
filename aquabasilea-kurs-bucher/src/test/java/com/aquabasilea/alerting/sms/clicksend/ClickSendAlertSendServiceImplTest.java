package com.aquabasilea.alerting.sms.clicksend;

import com.aquabasilea.alerting.api.AlertResponse;
import com.aquabasilea.alerting.api.AlertSendException;
import com.aquabasilea.alerting.config.AlertSendConfig;
import com.aquabasilea.alerting.send.AlertSendInfos;
import com.aquabasilea.test.server.DummyHttpServerTestCaseBuilder;
import com.aquabasilea.util.YamlUtil;
import org.junit.jupiter.api.Test;
import org.mockserver.model.Header;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class ClickSendAlertSendServiceImplTest {
   private static final String ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML = "alert/test-aquabasilea-alert-notification.yml";
   private static final String HOST = "http://127.0.0.1";
   private static final String POST = "POST";

   @Test
   void sendAlert() throws AlertSendException {
      // Given
      int port = 8181;
      String path = "/v3/send/sms";
      String msg = "test-message";
      AlertSendInfos alertSendInfos = getAlertSendInfos(msg);
      AlertSendConfig alertSendConfig = YamlUtil.readYaml(ALERT_TEST_AQUABASILEA_ALERT_NOTIFICATION_YML, AlertSendConfig.class);

      String response = "OK";
      String authValue = "Basic bnVsbDphYmMtMTIzLWFwaWtleQ==";// change if user/password from alert.keystore is changed
      DummyHttpServerTestCaseBuilder serverTestCaseBuilder = new DummyHttpServerTestCaseBuilder(port)
              .withHost(HOST)
              .withRequestResponse(path)
              .withMethod(POST)
              .withResponseBody(response)
              .withHeader(new Header("Authorization", authValue))
              .buildRequestResponse()
              .build();
      ClickSendAlertSendServiceImpl clickSendAlertSendServiceImpl = new ClickSendAlertSendServiceImpl(HOST + ":" + port + path);

      // When
      AlertResponse actualResponse = clickSendAlertSendServiceImpl.sendAlert(alertSendConfig, alertSendInfos);

      // Then
      assertThat(actualResponse.getStatus(), is(200));
      assertThat(actualResponse.getResponseEntity().toString(), is(response));
   }

   private static AlertSendInfos getAlertSendInfos(String msg) {
      List<String> receiver = List.of("0791234567");
      return new AlertSendInfos(msg, receiver);
   }
}