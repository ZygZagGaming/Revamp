package com.zygzag.revamp.util;

@FunctionalInterface
public interface Provider<T> {
    T provide();
}
