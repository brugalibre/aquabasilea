package com.aquabasilea.security.securestorage;


import com.aquabasilea.security.securestorage.util.KeyUtils;

public class AquabasileaKeyStore {

   public static final String AQUABASILEA_KEYSTORAGE_STORAGE = "aquabasilea-keystore.keystore";
   public static final String AQUABASILEA_KEYSTORE_ALIAS = "aquabasilea-keystore";

   public static void main(String[] args) {
      char[] aquabasileaKeyStoragePwd = args[0].toCharArray();
      KeyUtils.createAndLoadKeyStoreFromFile(AQUABASILEA_KEYSTORAGE_STORAGE, aquabasileaKeyStoragePwd);
      new WriteSecretToKeyStore().writeSecretToKeyStore(AQUABASILEA_KEYSTORAGE_STORAGE, aquabasileaKeyStoragePwd, AQUABASILEA_KEYSTORE_ALIAS, "my-key-store-pwd".toCharArray());
   }
}