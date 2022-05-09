package com.aquabasilea.security.securestorage.util;


import com.aquabasilea.security.securestorage.exception.SecureStorageException;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;

public class KeyUtils {

   private KeyUtils() {
      // private
   }
   /*
   Generate a new, empty keystore:
    keytool -genkey -alias <value-for-alias> -keyalg RSA -keysize 2048 -keystore <key-store-name>
    */

   public static final String ALGORITHM = "PBE";
   public static final String AQUABASILEA_KEYSTORAGE = "aquabasilea.keystore";
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

   public static KeyStore loadKeyStoreFromFile(String pathToFile, char[] keystorePassword) throws FileNotFoundException {
      try {
         KeyStore keyStore = KeyStore.getInstance(JCEKS);
         keyStore.load(getFileInputStreamForPath(pathToFile), keystorePassword);
         return keyStore;
      } catch (FileNotFoundException e) {
         throw e;
      } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
         throw new SecureStorageException(e);
      }
   }

   /**
    * Returns a {@link FileInputStream} from the given file path
    *
    * @param filePath the given file path
    * @return a {@link FileInputStream}
    * @throws FileNotFoundException if there is no such file
    */
   public static FileInputStream getFileInputStreamForPath(String filePath) throws FileNotFoundException {
      File file = new File(filePath);
      return new FileInputStream(file);
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