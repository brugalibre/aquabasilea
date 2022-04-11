module com.common.ui {
   requires javafx.controls;
   requires javafx.fxml;
   requires javafx.swing;
   requires javafx.base;
   exports com.common.ui.login.auth.control;
   exports com.common.ui.login.auth.view;
   exports com.common.ui.login.service;
}