package com.aquabasilea.application.security.securestorage;


import com.aquabasilea.application.security.securestorage.exception.SecureStorageException;
import com.aquabasilea.application.security.securestorage.util.KeyUtils;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

public class WriteSecretToKeyStore {

   private static final SecureRandom SECURE_RANDOM = new SecureRandom();

   public static void main(String[] args) {

      checkArgs(args);

      String pathToKeyStore = args[0];
      String keystorePassword = args[1];
      String alias = args[2];
      String aliasPassword = args[3];

      new WriteSecretToKeyStore().writeSecretToKeyStore(pathToKeyStore, keystorePassword.toCharArray(), alias, aliasPassword.toCharArray());
   }

   private static void checkArgs(String[] args) {
      if (args.length != 4) {
         throw new IllegalArgumentException("Usage: WritePasswordToKeyStore <full path to keystore> <keystore password> <key alias> <password to store>");
      }
   }

   /**
    * Writes the given password for the alias to the store
    *
    * @param pathToKeyStore   the path to the store
    * @param keyStorePassword the stores password
    * @param alias            the alias
    * @param aliasSecret      the secret for the alias to store
    */
   public void writeSecretToKeyStore(String pathToKeyStore, char[] keyStorePassword, String alias, char[] aliasSecret) {

      try {
         writeSecretToKeyStoreInternal(pathToKeyStore, keyStorePassword, alias, aliasSecret);
      } catch (NoSuchAlgorithmException | InvalidKeySpecException | KeyStoreException | IOException |
               CertificateException e) {
         throw new SecureStorageException(e);
      }
   }

   private static void writeSecretToKeyStoreInternal(String pathToKeyStore, char[] keyStorePassword, String alias, char[] aliasSecret)
           throws NoSuchAlgorithmException, InvalidKeySpecException, KeyStoreException, IOException, CertificateException {
      KeyStore keyStore = KeyUtils.loadKeyStoreFromFile(pathToKeyStore, keyStorePassword);
      PasswordProtection keyStorePP = new PasswordProtection(keyStorePassword);

      SecretKeyFactory factory = SecretKeyFactory.getInstance(KeyUtils.ALGORITHM);
      SecretKey generatedSecret = factory.generateSecret(new PBEKeySpec(
              aliasSecret,
              SECURE_RANDOM.generateSeed(512),
              13
      ));

      keyStore.setEntry(alias, new SecretKeyEntry(generatedSecret), keyStorePP);

      FileOutputStream outputStream = new FileOutputStream(pathToKeyStore);
      keyStore.store(outputStream, keyStorePassword);
      KeyUtils.clear(keyStorePassword, aliasSecret);
   }

   /**
    * Deletes the entry for the given alias in the given key-store
    *
    * @param pathToKeyStore   the path to the {@link KeyStore}
    * @param keyStorePassword the password for the key-store
    * @param alias            the alias
    */
   public void deleteSecretForAlias(String pathToKeyStore, char[] keyStorePassword, String alias) {
      try {
         extracted(pathToKeyStore, keyStorePassword, alias);
      } catch (NoSuchAlgorithmException | KeyStoreException | IOException | CertificateException e) {
         throw new SecureStorageException(e);
      }
   }

   private static void extracted(String pathToKeyStore, char[] keyStorePassword, String alias)
           throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
      KeyStore keyStore = KeyUtils.loadKeyStoreFromFile(pathToKeyStore, keyStorePassword);

      keyStore.deleteEntry(alias);
      FileOutputStream outputStream = new FileOutputStream(pathToKeyStore);
      keyStore.store(outputStream, keyStorePassword);
   }
}