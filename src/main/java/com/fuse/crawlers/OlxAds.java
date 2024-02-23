package com.fuse.crawlers;

import com.fuse.sql.erm.OlxAdEntityRelationalModel;
import com.fuse.sql.erm.OlxAdLinkEntityRelationalModel;
import com.fuse.sql.helpers.CrawlerHelper;
import com.fuse.sql.models.OlxAdModel;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class OlxAds implements com.fuse.sql.constants.OlxAds, Runnable {
    private static final Pattern cepPattern = Pattern.compile("[0-9]+", Pattern.CASE_INSENSITIVE);
    private static final OlxAdLinkEntityRelationalModel olxAdLinkEntityRelationalModel = new OlxAdLinkEntityRelationalModel();
    private static final CrawlerHelper crawlerHelper = new CrawlerHelper();
    private static final OlxAdEntityRelationalModel olxAdEntityRelationalModel = new OlxAdEntityRelationalModel();
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
        Logger logger = Logger.getLogger(OlxAdLinkEntityRelationalModel.class.getName());
        olxAdEntityRelationalModel.createTable();

        try (ResultSet allAdsResultSet = olxAdLinkEntityRelationalModel.selectAllAdLinks()) {
            WebDriver driver = new FirefoxDriver(crawlerHelper.firefoxOptions);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(crawlingTimeout));
            while (allAdsResultSet.next()) {
                boolean compatibleSiteVersion = true;

                OlxAdModel olxAdModel = new OlxAdModel();
                olxAdModel.skuId = allAdsResultSet.getLong(1);
                olxAdModel.link = allAdsResultSet.getString(2);
                olxAdModel.collectTimestamp = Timestamp.from(Instant.now());

                try {
                    Document adDocument = Jsoup.connect(olxAdModel.link)
                            .data("query", "ad")
                            .userAgent("Mozilla")
                            .timeout(3000)
                            .get();

                    Element olxAdJson = adDocument.getElementsByAttributeValueContaining("type", adJsonTypeValue).first();
                    if (Objects.requireNonNull(olxAdJson).attr("type").equals(adJsonTypeValue)) {
                        olxAdModel.json = new PGobject();
                        olxAdModel.json.setType("json");
                        olxAdModel.json.setValue(olxAdJson.data()
                                .replace("\"@context\":\"https://schema.org\",\"@type\":\"Product\",", "")
                                .replace("\"@type\":\"ImageObject\",", "")
                                .replace("\"@type\":\"Offer\",", ""));
                    }

                    JSONObject javaJsonObject = new JSONObject(Objects.requireNonNull(olxAdModel.json.getValue()));
                    olxAdModel.title = javaJsonObject.getString("name");
                    olxAdModel.description = javaJsonObject.getString("description");

                    try {
                        olxAdModel.price = Double.parseDouble(javaJsonObject
                                .getJSONObject("offers")
                                .getString("price")
                                .replace(",", "."));
                    } catch (JSONException exception) {
                        olxAdModel.price = null;
                    }

                    ArrayList<Object> imagesArray = new ArrayList<>();
                    for (Object imageObject : javaJsonObject.getJSONArray("image")) {
                        JSONObject jsonImageObject = new JSONObject(imageObject.toString());
                        imagesArray.add(jsonImageObject.getString("contentUrl"));
                    }

                    olxAdModel.images = olxAdEntityRelationalModel.createArrayOf(imagesArray, imagesArraySQLType);
                    olxAdModel.category = olxAdModel.link.split("/")[4];
                    olxAdModel.subcategory = olxAdModel.link.split("/")[5];

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
                        driver.manage().deleteAllCookies();
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
                } catch (IOException | WebDriverException exception) {
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