package com.fuse.crawlers;

import com.fuse.helpers.CrawlerHelper;
import com.fuse.sql.erm.OlxAdEntityRelationalModel;
import com.fuse.sql.erm.OlxAdLinkEntityRelationalModel;
import com.fuse.sql.models.OlxAdModel;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.postgresql.util.PGobject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public class OlxAds implements com.fuse.sql.constants.OlxAds, Runnable {
//public class OlxAds implements com.fuse.sql.constants.OlxAds {
    private static final Logger logger = Logger.getLogger(OlxAds.class.getName());
    private static final OlxAdLinkEntityRelationalModel olxAdLinkEntityRelationalModel = new OlxAdLinkEntityRelationalModel();
    private static final CrawlerHelper crawlerHelper = new CrawlerHelper();
    private static final OlxAdEntityRelationalModel olxAdEntityRelationalModel = new OlxAdEntityRelationalModel();
    public static final WebDriver driver = new FirefoxDriver(crawlerHelper.firefoxOptions);
//    public static final WebDriver driver = new FirefoxDriver();
    private static JSONObject getDetailsJSON(String details) {
        JSONObject detailsJSON = new JSONObject();
        String[] detailsList = details.split("\n");
        int detailsIndex = 0;
        while (detailsIndex < detailsList.length - 1) {
            if (detailsIndex + 1 <= detailsList.length - 1) {
                detailsJSON.put(detailsList[detailsIndex], detailsList[detailsIndex + 1]);
            }
            detailsIndex++;
        }
        return detailsJSON;
    }
    public void run() {
//    public static void  main(String[] args) {
        olxAdEntityRelationalModel.createTable();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(crawlingTimeout));

        try (ResultSet allAdsResultSet = olxAdLinkEntityRelationalModel.selectAllAdLinks()) {
            while (allAdsResultSet.next()) {
                driver.manage().deleteAllCookies();
                boolean compatibleSiteVersion = true;

                OlxAdModel olxAdModel = new OlxAdModel();
                olxAdModel.skuId = allAdsResultSet.getLong(1);
                olxAdModel.link = allAdsResultSet.getString(2);
                olxAdModel.collectTimestamp = Timestamp.from(Instant.now());

                try {
                    driver.get(olxAdModel.link);
                    Document adDocument = Jsoup.parse(driver.getPageSource());
                    Element olxAdJson = adDocument.getElementsByAttributeValueContaining("type", adJsonTypeValue).first();
                    if (olxAdJson != null) {
                        if (Objects.requireNonNull(olxAdJson).attr("type").equals(adJsonTypeValue)) {
                            olxAdModel.json = new PGobject();
                            olxAdModel.json.setType("json");
                            olxAdModel.json.setValue(olxAdJson.data()
                                    .replace("\"@context\":\"https://schema.org\",\"@type\":\"Product\",", "")
                                    .replace("\"@type\":\"ImageObject\",", "")
                                    .replace("\"@type\":\"Offer\",", ""));
                        }

                        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(olxAdModel.json.getValue()));
                        olxAdModel.title = jsonObject.getString("name");
                        olxAdModel.description = jsonObject.getString("description");

                        try {
                            olxAdModel.price = Double.parseDouble(jsonObject
                                    .getJSONObject("offers")
                                    .getString("price")
                                    .replace(",", "."));
                        } catch (JSONException exception) {
                            olxAdModel.price = null;
                        }

                        ArrayList<Object> imagesArray = new ArrayList<>();
                        for (Object imageObject : jsonObject.getJSONArray("image")) {
                            JSONObject jsonImageObject = new JSONObject(imageObject.toString());
                            imagesArray.add(jsonImageObject.getString("contentUrl"));
                        }

                        olxAdModel.images = olxAdEntityRelationalModel.createArrayOf(imagesArray, imagesArraySQLType);
                        olxAdModel.category = olxAdModel.link.split("/")[4];
                        try {
                            olxAdModel.subcategory = olxAdModel.link.split("/")[5];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            logger.severe("Couldn't fetch subcategory info");
                        }

                        // Selenium driver
                        driver.get(olxAdModel.link);

                        try {
                            olxAdModel.seller = driver.findElement(By.cssSelector(sellerMainCssSelector)).getText();
                        } catch (NoSuchElementException e) {
                            olxAdModel.seller = driver.findElement(By.cssSelector(sellerSecondaryCssSelector)).getText();
                            logger.severe("Couldn't fetch seller from new schema");
                        }

                        try {
                            String[] location = driver.findElement(By.cssSelector(locationMainCssSelector)).getText()
                                    .replace("Localização\n", "")
                                    .replace("Bairro\n", "")
                                    .replace("Município\n", "")
                                    .replace("CEP\n", "")
                                    .split("\n");

                            olxAdModel.cep = Long.parseLong(location[0]);
                            olxAdModel.city = location[1].split(", ")[0];
                            olxAdModel.neighbourhood = location[1].split(", ")[2];
                        } catch (NumberFormatException numberFormatException) {
                            compatibleSiteVersion = false;
                        } catch (NoSuchElementException e) {
                            try {
                                String[] location = driver.findElement(By.cssSelector(locationSecondaryCssSelector)).getText()
                                        .replace("Localização\n", "")
                                        .replace("Bairro\n", "")
                                        .replace("Município\n", "")
                                        .replace("CEP\n", "").split("\n");

                                olxAdModel.cep = Long.valueOf(location[0]);
                                olxAdModel.city = location[1];
                                olxAdModel.neighbourhood = location[2];

                                logger.severe("Couldn't fetch location from new schema");
                            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                                compatibleSiteVersion = false;
                            }
                        }

                        if (!compatibleSiteVersion) {
                            logger.severe("Incompatible portal, ignoring the ad, for now!");
                        } else {
                            String details;
                            try {
                                details = driver.findElement(By.cssSelector(detailsMainCssSelector)).getText()
                                        .replace("Detalhes", "").strip();
                            } catch (NoSuchElementException e) {
                                details = driver.findElement(By.cssSelector(detailsSecondaryCssSelector)).getText()
                                        .replace("Detalhes", "").strip();
                                logger.severe("Couldn't fetch details for new schema");
                            }

                            JSONObject detailsJSON = getDetailsJSON(details);

                            olxAdModel.details = new PGobject();
                            olxAdModel.details.setType("json");
                            olxAdModel.details.setValue(detailsJSON.toString());

                            Set<OlxAdModel> specificAdResult = olxAdEntityRelationalModel.selectSpecificAd(olxAdModel.skuId);

                            if (specificAdResult.isEmpty()) {
                                olxAdEntityRelationalModel.insertNewAd(olxAdModel);
                                olxAdLinkEntityRelationalModel.deleteSpecificAd(olxAdModel.skuId);
                            } else {
                                olxAdLinkEntityRelationalModel.deleteSpecificAd(olxAdModel.skuId);
                            }
                        }
                    } else {
                        logger.warning(String.format("%s ad was invalid, deleting it!", olxAdModel.skuId));
                        olxAdLinkEntityRelationalModel.deleteSpecificAd(olxAdModel.skuId);
                    }
                } catch (WebDriverException exception) {
                    olxAdLinkEntityRelationalModel.deleteSpecificAd(olxAdModel.skuId);
                    logger.severe(exception.toString());
                }
            }
            driver.quit();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }
}
