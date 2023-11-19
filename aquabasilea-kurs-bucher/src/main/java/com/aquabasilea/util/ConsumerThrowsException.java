package com.aquabasilea.util;

public interface ConsumerThrowsException<T, E extends Exception> {

   void accept(T t) throws E;
}
