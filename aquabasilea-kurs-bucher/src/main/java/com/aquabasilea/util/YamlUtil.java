package com.aquabasilea.util;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.InputStream;

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
      try (InputStream inputStream = FileUtil.getInputStream(ymlFile)) {
         return yaml.load(inputStream);
      } catch (IOException e) {
         throw new IllegalStateException(e);
      }
   }

   private static Representer createRepresenter() {
      Representer representer = new Representer();
      representer.getPropertyUtils().setSkipMissingProperties(true);
      return representer;
   }
}

