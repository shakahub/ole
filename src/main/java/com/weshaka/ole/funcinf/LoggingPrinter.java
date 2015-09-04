package com.weshaka.ole.funcinf;

@FunctionalInterface
public interface LoggingPrinter {
    public void print(String format, Object... args);
}
