package com.aquabasilea.alerting.sms.clicksend.data;

public class ClickSendSms {
   private String source;
   private String from;
   private String body;
   private String customString;
   private String to;

   public String getCustomString() {
      return customString;
   }

   public void setCustomString(String customString) {
      this.customString = customString;
   }

   public String getSource() {
      return source;
   }

   public void setSource(String source) {
      this.source = source;
   }

   public String getFrom() {
      return from;
   }

   public void setFrom(String from) {
      this.from = from;
   }

   public String getBody() {
      return body;
   }

   public void setBody(String body) {
      this.body = body;
   }

   public String getTo() {
      return to;
   }

   public void setTo(String to) {
      this.to = to;
   }
}

