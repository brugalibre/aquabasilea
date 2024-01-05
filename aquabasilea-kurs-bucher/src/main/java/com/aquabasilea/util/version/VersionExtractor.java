package com.aquabasilea.util.version;

public class VersionExtractor {

   /**
    * Extracts a {@link VersionDetails} from the given version value which must follow the semantic version pattern
    *
    * @param version the version value to extract
    * @return a {@link VersionDetails} from the given version value
    */
   public VersionDetails getVersionDetails(String version) {
      int major = Integer.parseInt(version.substring(0, version.indexOf(".")));
      int minor = Integer.parseInt(version.substring(version.indexOf(".") + 1, version.lastIndexOf(".")));
      int patch = Integer.parseInt(version.substring(version.lastIndexOf(".") + 1));
      return new VersionDetails(major, minor, patch);
   }

}
