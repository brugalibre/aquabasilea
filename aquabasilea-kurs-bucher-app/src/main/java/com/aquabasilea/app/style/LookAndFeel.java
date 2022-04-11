package com.aquabasilea.app.style;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

public class LookAndFeel {
   private LookAndFeel() {
      // private
   }

   /**
    * Defines {@link NimbusLookAndFeel} and a white background
    */
   public static void setNimbusLookAndFeel() {
      try {
         UIManager.setLookAndFeel(new NimbusLookAndFeel());
         UIManager.put("control", Color.WHITE);
      } catch (Exception ex) {
         try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
         } catch (Exception e) {
            throw new IllegalStateException(e);
         }
      }
   }
}
