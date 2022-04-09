package com.aquabasilea.ui.core.dialog;

import com.aquabasilea.ui.i18n.TextResources;
import com.aquabasilea.ui.util.SwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.TrayIcon.MessageType;

/**
 * The DialogManager is used to manage all JDialog in the user interface. Most
 * of them are object by the DialoManager.
 *
 * @author Dominic
 */
public class DialogManager extends JOptionPane {
   /**
    *
    */
   private static final long serialVersionUID = 1L;

   /**
    * Shows a OptionPane with the message 'messageToDisplay'. This method is
    * called, when a fatal error occurs, such as unable to load the pictures or
    * language files
    *
    * @param printStackTrace the printed stacktrace
    * @param title,          the title of the dialog
    */
   public static void showException(String printStackTrace, String title) {
      Toolkit.getDefaultToolkit().beep();
      showConfirmDialog(null, SwingUtil.getDisplayArea(TextResources.EXCEPTION_MSG, printStackTrace), title,
              CLOSED_OPTION, ERROR_MESSAGE);
   }

   public static void showDialog(Component parent, String messageTitle, String message, MessageType messageType) {
      showConfirmDialog(parent, message, messageTitle, CLOSED_OPTION, map2Int(messageType));
   }

   private static int map2Int(MessageType messageType) {
      switch (messageType) {
         case ERROR:
            break;
         case WARNING:
            return 2;
         case INFO:
            return 1;
         default:
            return -1;
      }
      return -1;
   }
}
