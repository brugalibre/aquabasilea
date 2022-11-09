package com.aquabasilea.service.security.login;

public class CredentialsNotValidException extends RuntimeException {
   public CredentialsNotValidException(String msg) {
      super(msg);
   }
}
