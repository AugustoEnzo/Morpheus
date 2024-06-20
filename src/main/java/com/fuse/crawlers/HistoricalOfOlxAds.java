package com.fuse.crawlers;

import com.fuse.sql.erm.HistoricalOfOlxAdsEntityRelationalModel;
import com.fuse.sql.erm.OlxAdEntityRelationalModel;
import com.fuse.helpers.CrawlerHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.postgresql.util.PGobject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

// TODO Finish implementation of Historical of olx ads

public class HistoricalOfOlxAds implements com.fuse.sql.constants.HistoricalOfOlxAds, Runnable {
    private static final Logger logger = Logger.getLogger(HistoricalOfOlxAds.class.getName());
    private static final CrawlerHelper crawlerHelper = new CrawlerHelper();
    private static final OlxAdEntityRelationalModel olxAdEntityRelationalModel = new OlxAdEntityRelationalModel();
    private static final HistoricalOfOlxAdsEntityRelationalModel historicalOfOlxAdsEntityRelationalModel = new HistoricalOfOlxAdsEntityRelationalModel();
    private static final WebDriver driver = new FirefoxDriver(crawlerHelper.firefoxOptions);
    public void run() {

        historicalOfOlxAdsEntityRelationalModel.createTable();

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(crawlingTimeout));

        try (ResultSet allAdsResultSet = olxAdEntityRelationalModel.selectAllAdLinks()) {
            while (allAdsResultSet.next()) {

                driver.manage().deleteAllCookies();

                com.fuse.sql.models.HistoricalOfOlxAds historicalOfOlxAds = new com.fuse.sql.models.HistoricalOfOlxAds();
                historicalOfOlxAds.skuId = allAdsResultSet.getLong(1);
                historicalOfOlxAds.link = allAdsResultSet.getString(2);
                historicalOfOlxAds.collectTimestamp = Timestamp.from(Instant.now());
                historicalOfOlxAds.oldJson = allAdsResultSet.getObject(4, PGobject.class);
                historicalOfOlxAds.oldPrice = allAdsResultSet.getDouble(7);
                historicalOfOlxAds.oldImages = allAdsResultSet.getArray(8);
                historicalOfOlxAds.title = allAdsResultSet.getString(5);
                historicalOfOlxAds.description = allAdsResultSet.getString(6);
                historicalOfOlxAds.seller = allAdsResultSet.getString(9);
                historicalOfOlxAds.category = allAdsResultSet.getString(10);
                historicalOfOlxAds.subcategory = allAdsResultSet.getString(11);
                historicalOfOlxAds.cep = allAdsResultSet.getLong(12);
                historicalOfOlxAds.city = allAdsResultSet.getString(13);
                historicalOfOlxAds.neighbourhood = allAdsResultSet.getString(14);
                historicalOfOlxAds.details = new PGobject();
                historicalOfOlxAds.details.setType("json");
                historicalOfOlxAds.details.setValue(allAdsResultSet.getObject(15, PGobject.class).getValue());

                try {
                    driver.get(historicalOfOlxAds.link);

                    Document adDocument = Jsoup.parse(driver.getPageSource());
                    Element olxAdJson = adDocument.getElementsByAttributeValueContaining("type", adJsonTypeValue).first();

                    if (olxAdJson != null) {
                        if (Objects.requireNonNull(olxAdJson).attr("type").equals(adJsonTypeValue)) {
                            String tempJSON = olxAdJson.data()
                                    .replace("\"@context\":\"https://schema.org\",\"@type\":\"Product\",", "")
                                    .replace("\"@type\":\"ImageObject\",", "")
                                    .replace("\"@type\":\"Offer\",", "");

                            if (!Objects.requireNonNull(tempJSON).equals(allAdsResultSet.getObject(4, PGobject.class).getValue())) {
                                historicalOfOlxAds.newJson = new PGobject();
                                historicalOfOlxAds.newJson.setType("json");
                                historicalOfOlxAds.newJson.setValue(tempJSON);
                            }

                            JSONObject jsonObject = new JSONObject(Objects.requireNonNull(tempJSON));

                            try {
                                historicalOfOlxAds.newPrice = Double.parseDouble(jsonObject
                                        .getJSONObject("offers")
                                        .getString("price")
                                        .replace(",", "."));
                            } catch (JSONException exception) {
                                logger.severe(exception.toString());
                            }

                            if (Objects.requireNonNull(historicalOfOlxAds.newPrice).equals(allAdsResultSet.getDouble(7))) {
                                historicalOfOlxAds.newPrice = null;
                            }

                            ArrayList<Object> imagesArray = new ArrayList<>();
                            for (Object imageObject : jsonObject.getJSONArray("image")) {
                                JSONObject jsonImageObject = new JSONObject(imageObject.toString());
                                imagesArray.add(jsonImageObject.getString("contentUrl"));
                            }

                            if (!Objects.requireNonNull(imagesArray).equals(allAdsResultSet.getArray(8))) {
                                historicalOfOlxAds.newImages = historicalOfOlxAdsEntityRelationalModel.createArrayOf(imagesArray, imagesArraySQLType);
                            }

                            historicalOfOlxAds.offline = false;
                        }
                    } else {
                        historicalOfOlxAds.offline = true;
                    }

                } catch (WebDriverException webDriverException) {
                    historicalOfOlxAds.offline = true;
                    logger.severe(webDriverException.toString());
                }

                historicalOfOlxAdsEntityRelationalModel.insertNewAd(historicalOfOlxAds);

                if (historicalOfOlxAds.offline) {
                    olxAdEntityRelationalModel.deleteSpecificAd(historicalOfOlxAds.skuId);
                }
            }
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }
}
