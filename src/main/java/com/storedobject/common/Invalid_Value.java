package com.storedobject.common;

@SuppressWarnings("serial")
public class Invalid_Value extends SOException {

    public Invalid_Value() {
        super();
    }

    public Invalid_Value(String message) {
        super(message);
    }
}