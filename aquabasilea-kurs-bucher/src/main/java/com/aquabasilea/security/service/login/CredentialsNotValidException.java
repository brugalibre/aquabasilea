package com.aquabasilea.security.service.login;

public class CredentialsNotValidException extends RuntimeException {
   public CredentialsNotValidException(String msg) {
      super(msg);
   }
}
