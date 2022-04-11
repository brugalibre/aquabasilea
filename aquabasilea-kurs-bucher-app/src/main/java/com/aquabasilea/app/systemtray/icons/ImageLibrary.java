package com.aquabasilea.app.systemtray.icons;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static java.util.Objects.requireNonNull;

/**
 * @author Dominic
 *
 */
public class ImageLibrary {

   private static Image bookingImage;
   private static Image idleImage;
   private static Image pausedImage;

   private ImageLibrary() {
      // Private constructor
   }

   /**
    * Loads the pictures. Therefore, it creates an entity of itself to load the
    * pictures in a none static method
    */
   public static void loadPictures() {
      URL idle = getUrlForPath("/idle.png");
      idleImage = new ImageIcon(idle).getImage();

      URL paused = getUrlForPath("/paused.png");
      pausedImage = new ImageIcon(paused).getImage();

      URL bookingUrl = getUrlForPath("/booking.png");
      bookingImage = new ImageIcon(bookingUrl).getImage();
   }

   private static URL getUrlForPath(String path) {
      return requireNonNull(ImageLibrary.class.getResource(getPath() + path), "Path '" + path + "' could not be mapped into URL!");
   }

   /**
    * @return a string which represents the path to the pictures
    */
   private static String getPath() {
      return "/images";
   }

   public static Image getBookingImage() {
      return bookingImage;
   }

   public static Image getIdleImage() {
      return idleImage;
   }

   public static Image getPaused() {
      return pausedImage;
   }
}
