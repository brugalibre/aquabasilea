package com.aquabasilea.rest.model.smsinbound;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record SmsInboundRequestDto(String timestamp, String to, String from, String body, String original_body,
                                   String original_message_id, String custom_string, String user_id, String subaccount_id,
                                   String message_id) {
}
