package com.aquabasilea.application.security.securestorage;

import com.aquabasilea.application.security.securestorage.util.KeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.util.function.Supplier;

/**
 * The {@link SecretStorage} contains password or other secrets within a {@link KeyStore}
 */
public class SecretStorage {
   private static final Logger LOG = LoggerFactory.getLogger(SecretStorage.class);
   private static final String EMPTY_SECRET = "";
   private final String pathToKeyStore;
   private final String pathToRootKeyStore;

   public SecretStorage(String pathToKeyStore, String pathToRootKeyStore) {
      this.pathToKeyStore = pathToKeyStore;
      this.pathToRootKeyStore = pathToRootKeyStore;
   }

   public static void main(String[] args) {
      char[] aquabasileaKeyStoragePwd = args[0].toCharArray();
      String pathToKeyStore = args[1];
      String alias = args[2];
      Supplier<char[]> userPasswordSupplier = new SecretStorage(pathToKeyStore, KeyUtils.AQUABASILEA_KEYSTORE_STORAGE).getSecretSupplier4Alias(alias, aquabasileaKeyStoragePwd);
      System.out.println("Userpwd: " + String.valueOf(userPasswordSupplier.get()));
   }

   /**
    * Returns a {@link Supplier} which holds the value for the given alias
    *
    * @param alias the alias for which the stored secret should be returned
    * @return a {@link Supplier} which holds the value for the given alias
    */
   public Supplier<char[]> getSecretSupplier4Alias(String alias, char[] aquabasileaKeyStoragePwd) {
      try {
         char[] keystorePassword = getKeystorePassword(aquabasileaKeyStoragePwd);
         KeyStore keyStore = KeyUtils.loadKeyStoreFromFile(pathToKeyStore, keystorePassword);
         return () -> new SecretFromKeyStoreReader().readSecretFromKeyStore(keyStore, keystorePassword, alias);
      } catch (FileNotFoundException e) {
         LOG.error(String.format("Secret for alias %s could not be retrieved!", alias), e);
         return EMPTY_SECRET::toCharArray;
      }
   }

   private char[] getKeystorePassword(char[] aquabasileaKeyStoragePwd) throws FileNotFoundException {
      KeyStore aquabasileaKeyStoreStorage = KeyUtils.loadKeyStoreFromFile(pathToRootKeyStore, aquabasileaKeyStoragePwd);
      return new SecretFromKeyStoreReader().readSecretFromKeyStore(aquabasileaKeyStoreStorage, aquabasileaKeyStoragePwd, KeyUtils.AQUABASILEA_KEYSTORE_STORAGE_ALIAS);
   }
}
