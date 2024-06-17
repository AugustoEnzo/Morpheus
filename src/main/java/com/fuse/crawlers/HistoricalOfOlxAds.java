package com.fuse.crawlers;

import com.fuse.sql.erm.HistoricalOfOlxAdsEntityRelationalModel;
import com.fuse.sql.erm.OlxAdEntityRelationalModel;
import com.fuse.helpers.CrawlerHelper;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.postgresql.util.PGobject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;

public class HistoricalOfOlxAds implements com.fuse.sql.constants.HistoricalOfOlxAds {
    private static final Logger logger = Logger.getLogger(HistoricalOfOlxAds.class.getName());
    private static final CrawlerHelper crawlerHelper = new CrawlerHelper();
    private static final OlxAdEntityRelationalModel olxAdEntityRelationalModel = new OlxAdEntityRelationalModel();
    private static final HistoricalOfOlxAdsEntityRelationalModel historicalOfOlxAdsEntityRelationalModel = new HistoricalOfOlxAdsEntityRelationalModel();
    private static final WebDriver driver = new FirefoxDriver(crawlerHelper.firefoxOptions);
    public static void main(String[] args) {
        historicalOfOlxAdsEntityRelationalModel.createTable();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(crawlingTimeout));
        try (ResultSet allAdsResultSet = olxAdEntityRelationalModel.selectAllAdLinks()) {
            while (allAdsResultSet.next()) {
                com.fuse.sql.models.HistoricalOfOlxAds historicalOfOlxAds = new com.fuse.sql.models.HistoricalOfOlxAds();
                historicalOfOlxAds.skuId = allAdsResultSet.getLong(2);
                historicalOfOlxAds.link = allAdsResultSet.getString(3);
                historicalOfOlxAds.collectTimestamp = Timestamp.from(Instant.now());

                try {

                } catch (WebDriverException webDriverException) {
                    historicalOfOlxAds.offline = true;
                    historicalOfOlxAdsEntityRelationalModel.insertNewAd(historicalOfOlxAds);
                    logger.severe(webDriverException.toString());
                }
            }
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }
}
