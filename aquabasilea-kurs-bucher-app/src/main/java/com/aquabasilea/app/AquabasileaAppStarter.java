package com.aquabasilea.app;

import javafx.application.Application;

/**
 * Wrapper to start the {@link AquabasileaCourseBookerFxApplication}
 * We can't start that directly, since then java complains and needs
 * the javafx runtime stuff
 */
public class AquabasileaAppStarter {

   public static void main(String[] args) {
      Application.launch(AquabasileaCourseBookerFxApplication.class, args);
   }
}
