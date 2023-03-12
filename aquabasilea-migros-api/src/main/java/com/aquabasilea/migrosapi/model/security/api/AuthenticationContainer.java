package com.aquabasilea.migrosapi.model.security.api;

import java.util.function.Supplier;

public record AuthenticationContainer(String username, Supplier<char[]> userPwdSupplier) {
}
