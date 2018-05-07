package com.storedobject.common;

public interface StringFiller {

    public default String fill(String name) {
        try {
            return StringUtility.stringify(getClass().getMethod("get" + name, (Class<?>[])null).invoke(this, (Object[])null));
        } catch (Throwable ignore) {
        }
        return "?";
    }
}
