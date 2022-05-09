package com.aquabasilea.security.securestorage;

import com.aquabasilea.security.securestorage.util.KeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.util.function.Supplier;

import static com.aquabasilea.security.securestorage.AquabasileaKeyStore.*;
import static com.aquabasilea.security.securestorage.util.KeyUtils.loadKeyStoreFromFile;

/**
 * The {@link SecretStorage} contains password or other secrets within a {@link KeyStore}
 */
public class SecretStorage {
   private static final Logger LOG = LoggerFactory.getLogger(SecretStorage.class);
   public static final String EMPTY_SECRET = "";
   private final String pathToKeyStore;

   public SecretStorage(String pathToKeyStore) {
      this.pathToKeyStore = pathToKeyStore;
   }

   public static void main(String[] args) {
      char[] aquabasileaKeyStoragePwd = args[0].toCharArray();
      Supplier<char[]> userPasswordSupplier = new SecretStorage(KeyUtils.AQUABASILEA_KEYSTORAGE).getSecretSupplier4Alias("username", aquabasileaKeyStoragePwd);
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
         KeyStore keyStore = loadKeyStoreFromFile(pathToKeyStore, keystorePassword);
         return () -> new SecretFromKeyStoreReader().readSecretFromKeyStore(keyStore, keystorePassword, alias);
      } catch (FileNotFoundException e) {
         LOG.error(String.format("Secret for alias %s could not be retrieved!", alias), e);
         return EMPTY_SECRET::toCharArray;
      }
   }

   private static char[] getKeystorePassword(char[] aquabasileaKeyStoragePwd) throws FileNotFoundException {
      KeyStore aquabasileaKeyStoreStorage = loadKeyStoreFromFile(AQUABASILEA_KEYSTORAGE_STORAGE, aquabasileaKeyStoragePwd);
      return new SecretFromKeyStoreReader().readSecretFromKeyStore(aquabasileaKeyStoreStorage, aquabasileaKeyStoragePwd, AQUABASILEA_KEYSTORE_ALIAS);
   }
}
