module ui-library {
   requires javafx.controls;
   requires javafx.fxml;
   requires javafx.swing;
   requires javafx.base;
   opens org.openjfx to javafx.fxml;
   exports org.openjfx;
}