package com.aquabasilea.util;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

import static java.util.Objects.isNull;

public class YamlUtil {

   private YamlUtil() {
      // privé
   }

   /**
    * Loads the given yml file and create a new class with the content
    * of the read file
    *
    * @param ymlFile the file
    * @param clazz   type of the class to create
    * @return a new instance of the given class, with the content of the given yml-file
    */
   public static <T> T readYaml(String ymlFile, Class<T> clazz) {
      Representer representer = createRepresenter();
      Yaml yaml = new Yaml(new Constructor(clazz), representer);
      try (InputStream inputStream = getInputStream(ymlFile)) {
         return yaml.load(inputStream);
      } catch (IOException e) {
         throw new IllegalStateException(e);
      }
   }

   /**
    * Loads the given yml file and create a new class with the content
    * of the read file
    * If there is no file, then a new instance of the given class is created, using its default constructor
    *
    * @param ymlFile the file
    * @param clazz   type of the class to create
    * @return a new instance of the given class, with the content of the given yml-file
    */
   public static <T> T readYamlIgnoreMissingFile(String ymlFile, Class<T> clazz) {
      try {
         return readYaml(ymlFile, clazz);
      } catch (Exception e) {
         try {
            return clazz.getDeclaredConstructor().newInstance();
         } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
            throw new IllegalStateException(e);
         }
      }
   }

   private static InputStream getInputStream(String ymlFile) throws IOException {
      InputStream resourceStreamFromResource = YamlUtil.class.getClassLoader().getResourceAsStream(ymlFile);
      if (isNull(resourceStreamFromResource)) {
         return new FileInputStream(ymlFile);
      }
      return resourceStreamFromResource;
   }

   private static Representer createRepresenter() {
      Representer representer = new Representer();
      representer.getPropertyUtils().setSkipMissingProperties(true);
      return representer;
   }
}
