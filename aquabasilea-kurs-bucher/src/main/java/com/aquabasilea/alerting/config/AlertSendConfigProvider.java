package com.aquabasilea.alerting.config;

/**
 * The {@link AlertSendConfigProvider} reads the {@link AlertSendConfig} from the file system
 * This interface encapsulates this process
 */
@FunctionalInterface
public interface AlertSendConfigProvider {
   /**
    * Returns the {@link AlertSendConfig} for the given users id
    *
    * @return the {@link AlertSendConfig} for the given users id
    */
   AlertSendConfig getAlertSendConfig();
}
