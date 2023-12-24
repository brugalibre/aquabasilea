package com.aquabasilea.application.security.model;

import java.util.function.Supplier;

public record AuthenticationContainer(String username, Supplier<char[]> userPwdSupplier) {
   // no-op
}
