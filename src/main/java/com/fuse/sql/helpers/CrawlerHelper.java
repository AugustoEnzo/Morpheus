package com.fuse.sql.helpers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CrawlerHelper {
    public String encodeUrl(String valuesToEncode) {
        return URLEncoder.encode(valuesToEncode, StandardCharsets.UTF_8);
    }
}
