package com.osmankartal.link_converter.domain.service;

import com.osmankartal.link_converter.domain.exception.LinkConversionBusinessException;
import com.osmankartal.link_converter.domain.model.LinkConversionConfig;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ShortlinkService {

    private static final int SHORTLINK_HASH_LENGTH = 10;
    private static final String SHORTLINK_HASH_CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    public String createShortlink() {
        return LinkConversionConfig.SHORTLINK_DOMAIN + "/" + createShortlinkHash();
    }

    private String createShortlinkHash() {
        StringBuilder sb = new StringBuilder(SHORTLINK_HASH_LENGTH);
        for (int i = 0; i < SHORTLINK_HASH_LENGTH; i++) {
            sb.append(SHORTLINK_HASH_CHARACTERS.charAt(RANDOM.nextInt(SHORTLINK_HASH_CHARACTERS.length())));
        }
        return sb.toString();
    }

    public void validateShortlink(String shortlink) {
        if (!shortlink.startsWith(LinkConversionConfig.SHORTLINK_DOMAIN)) {
            throw new LinkConversionBusinessException("shortlink must start with " + LinkConversionConfig.SHORTLINK_DOMAIN);
        }
    }
}
