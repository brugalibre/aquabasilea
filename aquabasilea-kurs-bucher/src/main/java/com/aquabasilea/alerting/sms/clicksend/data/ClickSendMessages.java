package com.aquabasilea.alerting.sms.clicksend.data;

import java.util.ArrayList;
import java.util.List;

public class ClickSendMessages {
   private List<ClickSendSms> messages;

   public ClickSendMessages (){
      this.messages = new ArrayList<>();
   }

   public List<ClickSendSms> getMessages() {
      return messages;
   }

   public void setMessages(List<ClickSendSms> messages) {
      this.messages = messages;
   }
}
