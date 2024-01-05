package com.aquabasilea.util.version;

public record VersionDetails(int major, int minor, int patch) implements Comparable<VersionDetails>{
   public boolean isGreater(VersionDetails versionDetailsTarget) {
      return major > versionDetailsTarget.major
              || (major == versionDetailsTarget.major && minor > versionDetailsTarget.minor)
              || (major == versionDetailsTarget.major && minor == versionDetailsTarget.minor && patch > versionDetailsTarget.patch);
   }

   @Override
   public int compareTo(VersionDetails versionDetailsOther) {
      return 0;
   }
}
