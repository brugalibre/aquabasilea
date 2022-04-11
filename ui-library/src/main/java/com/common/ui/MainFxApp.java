package com.common.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainFxApp extends Application {

   @Override
   public void start(Stage primaryStage) {
      primaryStage.setTitle("Java FX deployment test");

      Label label = new Label("App up & running.. fx is working fine!");
      Scene scene = new Scene(label, 500, 200);
      primaryStage.setScene(scene);

      primaryStage.show();
   }

   public static void main(String[] args) {
      Application.launch(args);
   }
}