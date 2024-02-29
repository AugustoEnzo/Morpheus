package com.fuse.helpers;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class CrawlerHelper {
    Logger logger = Logger.getLogger(CrawlerHelper.class.getName());
    public String encodeUrl(String valuesToEncode) {
        return URLEncoder.encode(valuesToEncode, StandardCharsets.UTF_8);
    }
    public Elements tryToGetValuesUsingCssSelectorOrXpathJsoupBased(String infoTarget, Document jsoupDocument, String cssSelector, String xPath) {
        Elements elements = jsoupDocument.select(cssSelector);
        if (elements.isEmpty() || !elements.hasText()) {
            logger.warning(String.format("Couldn't fetch %s info by css selector, trying by xpath instead", infoTarget));
            elements = jsoupDocument.selectXpath(xPath);
        } if (elements.isEmpty() || !elements.hasText()) {
           logger.severe(String.format("Couldn't fetch %s info by xpath too, please review the identifier into constants file", infoTarget));
        }
        return elements;
    };
    public final FirefoxOptions firefoxOptions = new FirefoxOptions()
            .addArguments("--headless", "--disable-gpu", "--reduce-security-for-testing", "--disable-web-security",
                    "--ignore-certificate-errors", "--blink-settings=imagesEnabled=false");
}
