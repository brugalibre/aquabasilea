package com.aquabasilea.util;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

import static java.util.Objects.isNull;

public class YamlUtil {

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

   /**
    * Stores the given object into the given yml-file
    *
    * @param object  the object to store
    * @param ymlFile the yml-file to store in
    * @param <T>     the type of the object
    */
   public static <T> void save2File(T object, String ymlFile) {
      DumperOptions dumperOptions = new DumperOptions();
      dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
      dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.DOUBLE_QUOTED);
      dumperOptions.setPrettyFlow(true);
      Yaml yaml = new Yaml(dumperOptions);
      try (PrintWriter writer = getPrintWriter(ymlFile)) {
         yaml.dump(object, writer);
      } catch (FileNotFoundException e) {
         throw new IllegalStateException(e);
      }
   }

   private static PrintWriter getPrintWriter(String ymlFile) throws FileNotFoundException {
      try {
         return new PrintWriter(ymlFile);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
         return new PrintWriter("/src/main/resources/" + ymlFile);
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
