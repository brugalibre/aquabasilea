package com.aquabasilea.migrosapi.v1.model.security;

import java.util.function.Supplier;

public record AuthenticationContainer(String username, Supplier<char[]> userPwdSupplier) {
}
