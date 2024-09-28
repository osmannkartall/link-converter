package com.osmankartal.link_converter.adapter.test;

import com.osmankartal.link_converter.domain.model.LinkConversionConfig;

public class LinkConversionTestConfig {

    public static final String EXAMPLE_URL = LinkConversionConfig.BASE_URL + "/item/1";
    public static final String EXAMPLE_DEEPLINK = LinkConversionConfig.BASE_DEEPLINK + "item&id=1";
    public static final String EXAMPLE_SHORTLINK = LinkConversionConfig.SHORTLINK_DOMAIN + "/zr9h8x621c";

    private LinkConversionTestConfig() {
        throw new IllegalStateException("LinkConversionTestConfig is configuration class");
    }

}