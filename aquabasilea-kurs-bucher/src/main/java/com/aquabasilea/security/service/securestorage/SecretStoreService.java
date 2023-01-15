package com.aquabasilea.security.service.securestorage;


import com.aquabasilea.security.securestorage.SecretFromKeyStoreReader;
import com.aquabasilea.security.securestorage.util.KeyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyStore;

@Service
public class SecretStoreService {

   @Value("${application.security.keyStorePassword}")
   private String keyStorePassword;

   @Value("${application.security.aquabasileaKeyStoreName}")
   private String aquabasileaKeyStoreName;

   private final SecretFromKeyStoreReader secretFromKeyStoreReader;

   /**
    * Constructor for testing purpose only!
    *
    * @param keyStorePassword        the password of the key-storage
    * @param aquabasileaKeyStoreName the name/path of the key-storage
    */
   public SecretStoreService(String keyStorePassword, String aquabasileaKeyStoreName) {
      this.keyStorePassword = keyStorePassword;
      this.aquabasileaKeyStoreName = aquabasileaKeyStoreName;
      this.secretFromKeyStoreReader = new SecretFromKeyStoreReader();
   }

   public SecretStoreService() {
      this.secretFromKeyStoreReader = new SecretFromKeyStoreReader();
   }

   public char[] getUserPassword(String username) {
      KeyStore keyStore = KeyUtils.loadKeyStoreFromFileOrThrow(aquabasileaKeyStoreName, keyStorePassword.toCharArray());
      return secretFromKeyStoreReader.readSecretFromKeyStore(keyStore, keyStorePassword.toCharArray(), username);
   }
}