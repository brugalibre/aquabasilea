package com.zeiterfassung.web.common.navigate.util;

import org.openqa.selenium.By;

public class WebNavigateUtil {

   private static final int SLEEP_INTERVAL = 500;

   private WebNavigateUtil() {
      // Private
   }

   /**
    * Waits the given amount of miliseconds. If a {@link InterruptedException} occures, this is thrown as a {@link IllegalStateException}
    *
    * @param timeInMilis the time to wait
    */
   public static void waitForMiliseconds(int timeInMilis) {
      try {
         Thread.sleep(timeInMilis);
      } catch (InterruptedException e) {
         throw new IllegalStateException(e);
      }
   }

   /**
    * Creates a {@link By#xpath(String)} for the given tag name, attribute name and value
    * The created xpath looks like
    * <code>//tagName[@attrName='attrValue']</code>
    *
    * @param tagName   the elements tag, e.g. <div>
    * @param attrName  the name of the custom attribute
    * @param attrValue the value of the attribute
    * @return a {@link By#xpath(String)} for the given tag name, attribute name and value
    */
   public static By createXPathBy(String tagName, String attrName, String attrValue) {
      return By.xpath("//" + tagName + "[@" + attrName + "='" + attrValue + "']");
   }

   /**
    * Creates a {@link By#xpath(String)} for the given tag name, attribute name and value
    * The created xpath looks like
    * <code>//tagName[starts-with(@attrName, "attrValue")]</code>
    *
    * @param tagName   the elements tag, e.g. <div>
    * @param attrName  the name of the custom attribute
    * @param attrValue the value of the attribute
    * @return a {@link By#xpath(String)} for the given tag name, attribute name and value
    */
   public static By createStartsWithXPathBy(String tagName, String attrName, String attrValue) {
      return By.xpath("//" + tagName + "[starts-with(@" + attrName + ",'" + attrValue + "')]");
   }
}
