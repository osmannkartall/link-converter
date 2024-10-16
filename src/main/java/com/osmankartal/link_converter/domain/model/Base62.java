package com.osmankartal.link_converter.domain.model;

public class Base62 {

    private Base62() { }

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String encode(long value) {
        if (value == 0) {
            return "0";
        }

        if (value < 0) {
            throw new IllegalArgumentException("Cannot handle negative numbers");
        }

        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(BASE62_CHARS.charAt((int) (value % 62)));
            value /= 62;
        }
        return sb.reverse().toString();
    }

}
