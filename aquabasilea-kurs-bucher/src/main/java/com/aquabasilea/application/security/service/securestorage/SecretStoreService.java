package com.aquabasilea.application.security.service.securestorage;


import com.aquabasilea.application.security.securestorage.SecretFromKeyStoreReader;
import com.aquabasilea.application.security.securestorage.util.KeyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyStore;

@Service
public class SecretStoreService {

   private final String keyStorePassword;
   private final String aquabasileaKeyStoreName;

   private final SecretFromKeyStoreReader secretFromKeyStoreReader;

   /**
    * Default constructor of the {@link SecretStoreService}
    *
    * @param keyStorePassword        the password of the key-storage
    * @param aquabasileaKeyStoreName the name/path of the key-storage
    */
   public SecretStoreService(@Value("${application.security.keyStorePassword}") String keyStorePassword,
                             @Value("${application.security.aquabasileaKeyStoreName}") String aquabasileaKeyStoreName) {
      this.keyStorePassword = keyStorePassword;
      this.aquabasileaKeyStoreName = aquabasileaKeyStoreName;
      this.secretFromKeyStoreReader = new SecretFromKeyStoreReader();
   }

   public char[] getUserPassword(String username) {
      KeyStore keyStore = KeyUtils.loadKeyStoreFromFileOrThrow(aquabasileaKeyStoreName, keyStorePassword.toCharArray());
      return secretFromKeyStoreReader.readSecretFromKeyStore(keyStore, keyStorePassword.toCharArray(), username);
   }
}