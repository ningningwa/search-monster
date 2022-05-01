package org.xk.crawler.utils;

import java.util.UUID;

public class IDUtils {
    public static String getUUID() {
        System.out.println(UUID.randomUUID());
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }
}
