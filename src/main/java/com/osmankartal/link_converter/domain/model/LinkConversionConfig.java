package com.osmankartal.link_converter.domain.model;


public class LinkConversionConfig {

    public static final String BASE_DEEPLINK = "app://";
    public static final String BASE_URL = "https://any.domain.com";
    public static final String SHORTLINK_DOMAIN = "http://localhost:8080";

    private LinkConversionConfig() {
        throw new IllegalStateException("LinkConversionConfig is configuration class");
    }
}
