package com.aquabasilea.ui.util;

import com.aquabasilea.ui.core.dialog.DialogManager;
import com.aquabasilea.ui.i18n.TextResources;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * An util-class for usage with {@link Exception}s. E.g is it possible to show
 * an uncaught Exception to the user.
 *
 * @author Dominic
 */
public class ExceptionUtil {

   private ExceptionUtil() {
      // private 
   }

   /**
    * @param thread the {@link Thread} which caused the Exception
    * @param thrown the {@link Throwable}
    */
   public static void showException(Thread thread, Throwable thrown) {
      String printStackTrace = getStackTracesAsString(thread.getName(), thrown);
      DialogManager.showException(printStackTrace, TextResources.EXCEPTION_DIALOG_TITLE);
   }

   /**
    * Creates and returns a {@link String} from a given Stack trace.
    *
    * @param thread, the {@link Thread} which caused the {@link Throwable}
    * @param thrown, the thrown {@link Throwable}
    * @return a {@link String} that includes the print-stack-trace
    */
   public static String getStackTracesAsString(String thread, Throwable thrown) {
      StringBuilder stackTrace = new StringBuilder("Exception in Thread \"" + thread + "\" ");
      StringWriter ws = new StringWriter();
      PrintWriter pw = new PrintWriter(ws);
      thrown.printStackTrace(pw);

      stackTrace.append(ws);
      return stackTrace.toString();
   }
}
