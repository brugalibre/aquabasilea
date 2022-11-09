package com.aquabasilea.util;

import com.aquabasilea.security.securestorage.util.KeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class FileUtil {
   private static final Logger LOG = LoggerFactory.getLogger(KeyUtils.class);

   private FileUtil() {
      // private
   }

   /**
    * Returns a {@link FileInputStream} from the given file path
    *
    * @param filePath the given file path
    * @return a {@link FileInputStream}
    * @throws FileNotFoundException if there is no such file
    * @throws URISyntaxException    if we were not able to create a URI from the file-path
    */
   public static FileInputStream getFileInputStreamForPath(String filePath) throws FileNotFoundException, URISyntaxException {
      File file = new File(filePath);
      try {
         return new FileInputStream(file);
      } catch (Exception e) {
         LOG.error("Error while creating FileInputStream for file. Try loading with classLoader.getResource..", e);
      }
      URL urlResource = KeyUtils.class.getClassLoader().getResource(filePath);
      URI resource = requireNonNull(urlResource, "url not found for file '" + filePath + "'").toURI();
      return new FileInputStream(new File(resource));
   }

   /**
    * Creates a new {@link InputStream} for the given path to a file, depending if the file is located in the (test)-resources or
    * on the os-file path
    *
    * @param filePath the path to the desired file
    * @return an {@link InputStream} to the given file path
    * @throws IOException if the file could not be loaded
    */
   public static InputStream getInputStream(String filePath) throws IOException {
      InputStream resourceStreamFromResource = KeyUtils.class.getClassLoader().getResourceAsStream(filePath);
      if (isNull(resourceStreamFromResource)) {
         return new FileInputStream(filePath);
      }
      return resourceStreamFromResource;
   }
}
