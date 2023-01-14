package com.aquabasilea.security.securestorage.util;


import com.aquabasilea.security.securestorage.exception.SecureStorageException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import static com.brugalibre.util.file.FileUtil.getFileInputStreamForPath;

public class KeyUtils {

   private KeyUtils() {
      // private
   }

   /*
   Generate a new, empty keystore:
   keytool -genkeypair -alias aquabasilea-alert -storepass test123 -keypass secretPassword -keystore emptyStore.keystore -dname "CN=Developer, OU=Department, O=Company, L=City, ST=State, C=CA"
   // Optional -keysize 2048 argument
   keytool -genkeypair -alias aquabasilea-alert -storepass test123 -keypass test123 -keystore aquabasilea-alert.keystore
   keytool -delete -alias aquabasilea-keystore -keystore aquabasilea-keystore.keystore
    keytool -genkey -alias <value-for-alias> -keyalg RSA -keysize 2048 -keystore <key-store-name>
    */

   public static final String ALGORITHM = "PBE";
   /**
    * Note: Changing name here also requires changing name in the application.yml!
    */
   public static final String AQUABASILEA_ALERT_KEYSTORE = "aquabasilea-alert.keystore";
   public static final String AQUABASILEA_KEYSTORE_STORAGE = "aquabasilea-keystore.keystore";
   public static final String AQUABASILEA_KEYSTORE_STORAGE_ALIAS = "aquabasilea-keystore";
   public static final String JCEKS = "PKCS12";

   /**
    * Creates a new empty {@link KeyStore} with the given password at the given location
    *
    * @param pathToFile       the path and name of the {@link KeyStore} to create
    * @param keystorePassword the {@link KeyStore}s password
    * @return the new created instance of a {@link KeyStore}
    */
   public static KeyStore createAndLoadKeyStoreFromFile(String pathToFile, char[] keystorePassword) {
      try {
         KeyStore keyStore = KeyStore.getInstance(JCEKS);
         keyStore.load(null, keystorePassword);
         try (FileOutputStream fos = new FileOutputStream(pathToFile)) {
            keyStore.store(fos, keystorePassword);
         }
         return keyStore;
      } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
         throw new SecureStorageException(e);
      }
   }

   /**
    * Loads a {@link KeyStore} with the given path/name and key-store password or throws a {@link IllegalStateException} if there
    * is no {@link KeyStore}
    *
    * @param pathToFile       the path or name of the {@link KeyStore}
    * @param keystorePassword the password for the {@link KeyStore}
    * @return a {@link KeyStore} instance
    */
   public static KeyStore loadKeyStoreFromFileOrThrow(String pathToFile, char[] keystorePassword) {
      KeyStore keyStore;
      try {
         keyStore = loadKeyStoreFromFile(pathToFile, keystorePassword);
      } catch (FileNotFoundException e) {
         throw new IllegalStateException(e);
      }
      return keyStore;
   }

   public static KeyStore loadKeyStoreFromFile(String pathToFile, char[] keystorePassword) throws FileNotFoundException {
      try {
         KeyStore keyStore = KeyStore.getInstance(JCEKS);
         keyStore.load(getFileInputStreamForPath(pathToFile), keystorePassword);
         return keyStore;
      } catch (KeyStoreException | URISyntaxException | IOException | NoSuchAlgorithmException | CertificateException e) {
         throw new SecureStorageException(e);
      }
   }

   /**
    * Clears the given char arrays
    *
    * @param charArrays the char arrays to clear
    */
   public static void clear(char[]... charArrays) {
      for (char[] charArray : charArrays) {
         Arrays.fill(charArray, '0');
      }
   }
}