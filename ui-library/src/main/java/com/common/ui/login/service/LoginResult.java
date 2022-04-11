package com.common.ui.login.service;

public class LoginResult {
   private boolean success;
   private String username;
   private char[] userPwd;

   public LoginResult(boolean success, String username, char[] userPwd) {
      this.success = success;
      this.username = username;
      this.userPwd = userPwd;
   }

   public boolean isSuccess() {
      return success;
   }

   public String getUsername() {
      return username;
   }

   public char[] getUserPwd() {
      return userPwd;
   }
}
