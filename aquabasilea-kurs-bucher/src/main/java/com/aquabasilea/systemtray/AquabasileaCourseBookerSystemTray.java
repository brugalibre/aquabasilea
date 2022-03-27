package com.aquabasilea.systemtray;

import com.aquabasilea.coursebooker.AquabasileaCourseBooker;
import com.aquabasilea.coursebooker.callback.CourseBookingStateChangedHandler;
import com.aquabasilea.coursebooker.states.CourseBookingState;
import com.aquabasilea.i18n.TextResources;
import com.aquabasilea.systemtray.icons.ImageLibrary;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * @author Dominic
 */
public class AquabasileaCourseBookerSystemTray implements CourseBookingStateChangedHandler {
   private AquabasileaCourseBooker aquabasileaCourseBooker;
   private MenuItem pauseOrSuspendCourseBookerItem;
   private MenuItem refreshCoursesItem;
   private TrayIcon trayIcon;

   public void registerSystemtray(AquabasileaCourseBooker aquabasileaCourseBooker) {

      this.aquabasileaCourseBooker = aquabasileaCourseBooker;
      setLookAndFeel();
      addTrayIcon2SystemTray();

      // Create a popup menu components
      MenuItem exitItem = createExitMenu();
      createStartBookingMenuItem();
      createPauseBookingMenuItem();
      PopupMenu popupMenu = createPopupMenu(exitItem);

      trayIcon.setPopupMenu(popupMenu);
      trayIcon.addMouseMotionListener(getMouseMotionListener());
   }

   @Override
   public void onCourseBookingStateChanged(CourseBookingState courseBookingState) {
      this.updateTrayIconState();
   }

   //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   // Create Content for UI
   //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   private MouseMotionListener getMouseMotionListener() {
      return new MouseMotionListener() {

         @Override
         public void mouseMoved(MouseEvent arg0) {
            trayIcon.setToolTip(aquabasileaCourseBooker.getInfoString4State());
         }

         @Override
         public void mouseDragged(MouseEvent mouseEvent) {
         }
      };
   }

   private MenuItem createExitMenu() {
      MenuItem exitItem = new MenuItem(TextResources.EXIT_APP);
      exitItem.addActionListener(actionEvent -> {
         this.aquabasileaCourseBooker.stop();
         SystemTray.getSystemTray().remove(this.trayIcon);
         System.exit(0);
      });
      return exitItem;
   }

   private void createStartBookingMenuItem() {
      refreshCoursesItem = new MenuItem(TextResources.REFRESH_COURSES_LABEL);
      refreshCoursesItem.addActionListener(actionEvent -> refreshCourses());
      refreshCoursesItem.setEnabled(true);
   }

   private void createPauseBookingMenuItem() {
      pauseOrSuspendCourseBookerItem = new MenuItem(TextResources.PAUSE_APP);
      pauseOrSuspendCourseBookerItem.addActionListener(actionEvent -> this.aquabasileaCourseBooker.pauseOrResume());
      pauseOrSuspendCourseBookerItem.setEnabled(true);
   }

   private void refreshCourses() {
      this.aquabasileaCourseBooker.refreshCourses();
   }

   private void updateTrayIconState() {
      if (aquabasileaCourseBooker.isBookingCourse()) {
         trayIcon.setImage(ImageLibrary.getBookingImage());
      } else if (aquabasileaCourseBooker.isBookingCourseDryRun()) {
         trayIcon.setImage(ImageLibrary.getBookingImage());
      } else if (aquabasileaCourseBooker.isPaused()) {
         trayIcon.setImage(ImageLibrary.getPaused());
         pauseOrSuspendCourseBookerItem.setLabel(TextResources.RESUME_APP);
      } else {
         trayIcon.setImage(ImageLibrary.getIdleImage());
      }
      this.refreshCoursesItem.setEnabled(aquabasileaCourseBooker.isIdle());
      this.pauseOrSuspendCourseBookerItem.setEnabled(aquabasileaCourseBooker.isIdle() || aquabasileaCourseBooker.isPaused());
   }

   private void addTrayIcon2SystemTray() {
      try {
         SystemTray tray = SystemTray.getSystemTray();
         trayIcon = new TrayIcon(ImageLibrary.getIdleImage(), TextResources.APP_NAME + ": " + aquabasileaCourseBooker.getInfoString4State());
         tray.add(trayIcon);
      } catch (AWTException e) {
         throw new IllegalStateException(e);
      }
   }

   private PopupMenu createPopupMenu(MenuItem exitItem) {
      PopupMenu popupMenu = new PopupMenu();
      popupMenu.add(refreshCoursesItem);
      popupMenu.add(pauseOrSuspendCourseBookerItem);
      popupMenu.addSeparator();
      popupMenu.add(exitItem);
      return popupMenu;
   }

   private void setLookAndFeel() {
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
