package com.fuse.crawlers;

import com.fuse.sql.erm.AliexpressAdLinkEntityRelationalModel;
import com.fuse.sql.helpers.CrawlerHelper;
import com.fuse.sql.models.AliexpressAdLinkModel;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public class AliexpressAdsLinks implements com.fuse.sql.constants.AliexpressAdsLinks, Runnable {
    private static final CrawlerHelper crawlerHelper = new CrawlerHelper();
    public void run() {
        Logger logger = Logger.getLogger(AliexpressAdsLinks.class.getName());

        AliexpressAdLinkEntityRelationalModel aliexpressAdLinkEntityRelationalModelEntityRelationModel = new AliexpressAdLinkEntityRelationalModel();

        // Firefox options to use headless mode and avoid loading images
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless", "--disable-gpu", "--blink-settings=imagesEnabled=false");

        WebDriver driver = new FirefoxDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        int actualLastPage = lastPageConstant;

        List<String> itemsToQuery = new ArrayList<>();
        itemsToQuery.add("PC Gaming");
        itemsToQuery.add("Peças de computador");
        itemsToQuery.add("Ferramentas");
        itemsToQuery.add("Refletor LED solar");

        for (String item : itemsToQuery) {
            logger.info("Querying for '" + item + "' products");
            String aliexpressURLBeginning = String.format("https://aliexpress.com/w/wholesale-%s.html?isFromCategory=y&categoryUrlParams=", item);
            String aliexpressURLEnding = String.format("&page=1&g=y&SearchText=%s&language=en_US", item);

            String url = aliexpressURLBeginning + crawlerHelper.encodeUrl(aliexpressQueryParams + aliexpressURLEnding);


            aliexpressAdLinkEntityRelationalModelEntityRelationModel.createAliAdsLinksTable();

            boolean pageWasScrapped;
            for (int pageIndex = 1; pageIndex <= actualLastPage; pageIndex++) {
                pageWasScrapped = false;
                while (!pageWasScrapped) {
                    try {
                        logger.info("Querying page: " + pageIndex);

                        driver.get(url.replaceFirst("&page=[0-9]", "&page=" + pageIndex));

                        // Wait for card list to load
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(cardListDivXPath)));

                        // Wait for last page anchor to load
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(lastPageAnchorXPath)));

                        // For first page only get last page number
                        if (pageIndex == 1) {
                            actualLastPage = Integer.parseInt(driver.findElement(By.xpath(lastPageAnchorXPath)).getText());
                        }

                        for (int adIndex = 1; adIndex <= adsLimitPerPage; adIndex++) {
                            AliexpressAdLinkModel adLinkModel = new AliexpressAdLinkModel();
                            try {
                                WebElement adLinkWebElement = By.xpath(String.format("/html/body/div[6]/div[1]/div/div[2]/div[2]/div/div[%d]/div/a", adIndex)).findElement(driver);
                                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", adLinkWebElement);

                                adLinkModel.link = adLinkWebElement.getAttribute("href");
                                adLinkModel.skuId = Long.parseLong(adLinkModel.link.split("%7B%22sku_id%22%3A%22")[1]
                                        .replaceFirst("%22%7D", "")
                                        .replaceFirst("&utparam-url=scene%3Asearch%7Cquery_from%3A", ""));
                                adLinkModel.collectTimestamp = java.sql.Timestamp.from(Instant.now());

                                // Verify if the ad was already mapped
                                Set<AliexpressAdLinkModel> specificAdResult = aliexpressAdLinkEntityRelationalModelEntityRelationModel.selectSpecificAd(adLinkModel.skuId);

                                if (specificAdResult.isEmpty()) {
                                    aliexpressAdLinkEntityRelationalModelEntityRelationModel.insertNewAd(adLinkModel);

                                    // If product was already inserted into ali_ads_link, but the link is different update with newer link
                                } else if (specificAdResult.stream().findFirst().get().skuId != adLinkModel.skuId && !Objects.equals(specificAdResult.stream().findFirst().get().link, adLinkModel.link)) {
                                    aliexpressAdLinkEntityRelationalModelEntityRelationModel.updateSpecificSku(adLinkModel);
                                }
                            } catch (NoSuchElementException noSuchElementException) {
                                logger.severe(noSuchElementException.toString());
                            }
                        }
                        pageWasScrapped = true;
                    } catch (WebDriverException webDriverException) {
                        logger.severe(webDriverException.toString());
                    }
                }
            }
        }
        // Driver killer
        driver.quit();
        logger.fine("Finished querying for the wanted ads");
    }
}