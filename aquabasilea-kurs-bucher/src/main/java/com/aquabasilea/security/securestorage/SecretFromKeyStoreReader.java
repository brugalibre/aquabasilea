package com.aquabasilea.security.securestorage;


import com.aquabasilea.security.securestorage.exception.SecureStorageException;
import com.aquabasilea.security.securestorage.util.KeyUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.spec.InvalidKeySpecException;

import static com.aquabasilea.security.securestorage.util.KeyUtils.ALGORITHM;
import static com.aquabasilea.security.securestorage.util.KeyUtils.loadKeyStoreFromFile;

public class SecretFromKeyStoreReader {

   public static void main(String[] args) throws Exception {

      checkArgs(args);

      String pathToKeyStore = args[0];
      String keystorePassword = args[1];
      String alias = args[2];

      KeyStore keyStore = loadKeyStoreFromFile(pathToKeyStore, keystorePassword.toCharArray());

      char[] secret = new SecretFromKeyStoreReader().readSecretFromKeyStore(keyStore, keystorePassword.toCharArray(), alias);
      System.out.println("read password '" + String.valueOf(secret) + "'");
   }

   public char[] readSecretFromKeyStore(KeyStore keyStore, char[] aliasPassword, String passwordAlias) {
      try {
         return readSecretFromKeyStoreInternal(keyStore, aliasPassword, passwordAlias);
      } catch (NoSuchAlgorithmException | InvalidKeySpecException | UnrecoverableEntryException | KeyStoreException e) {
         throw new SecureStorageException(e);
      }
   }

   private static char[] readSecretFromKeyStoreInternal(KeyStore keyStore, char[] aliasPassword, String passwordAlias) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, InvalidKeySpecException {
      PasswordProtection keyStorePP = new PasswordProtection(aliasPassword);
      SecretKeyEntry ske = (SecretKeyEntry) keyStore.getEntry(passwordAlias, keyStorePP);

      SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
      PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(ske.getSecretKey(),
              PBEKeySpec.class);

      KeyUtils.clear(aliasPassword);
      return keySpec.getPassword();
   }

   private static void checkArgs(String[] args) {
      if (args.length != 3) {
         throw new IllegalArgumentException("Usage: SecretFromKeyStoreReader <full path to keystore> <keystore secret> <key alias>");
      }
   }
}