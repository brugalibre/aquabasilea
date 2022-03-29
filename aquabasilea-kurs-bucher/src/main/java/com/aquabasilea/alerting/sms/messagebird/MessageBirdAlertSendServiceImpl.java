package com.aquabasilea.alerting.sms.messagebird;

import com.aquabasilea.alerting.api.AlertSendException;
import com.aquabasilea.alerting.config.AlertSendConfig;
import com.aquabasilea.alerting.api.AlertSendService;
import com.messagebird.MessageBirdClient;
import com.messagebird.MessageBirdService;
import com.messagebird.MessageBirdServiceImpl;
import com.messagebird.exceptions.GeneralException;
import com.messagebird.exceptions.UnauthorizedException;
import com.messagebird.objects.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sms alert API From MessageBird
 */
public class MessageBirdAlertSendServiceImpl implements AlertSendService {
   private static final Logger LOG = LoggerFactory.getLogger(MessageBirdAlertSendServiceImpl.class);

   public static void main(String[] args) {

   }

   @Override
   public String sendAlert(AlertSendConfig alertSendConfig, String msg) throws AlertSendException {
      LOG.info("Sending text {} to {} receivers", msg, alertSendConfig.getReceivers().size());
      MessageBirdService messageBirdService = new MessageBirdServiceImpl(alertSendConfig.getApiKey());
      MessageBirdClient messageBirdClient = new MessageBirdClient(messageBirdService);
      // convert String number into acceptable format
      List<BigInteger> receivers = alertSendConfig.getReceivers()
              .stream()
              .map(BigInteger::new)
              .collect(Collectors.toList());
      try {
         final MessageResponse response = messageBirdClient.sendMessage(alertSendConfig.getOriginator(), msg, receivers);
         LOG.info("Sending text done, response '{}'", response);
         return response.getBody();
      } catch (UnauthorizedException | GeneralException e) {
         throw new AlertSendException(e);
      }
   }
}
