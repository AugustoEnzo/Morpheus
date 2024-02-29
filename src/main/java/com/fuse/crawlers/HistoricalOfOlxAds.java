package com.fuse.crawlers;

import com.fuse.sql.erm.OlxAdEntityRelationalModel;
import com.fuse.helpers.CrawlerHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class HistoricalOfOlxAds implements com.fuse.sql.constants.HistoricalOfOlxAds {
    private static final CrawlerHelper crawlerHelper = new CrawlerHelper();
    private static final OlxAdEntityRelationalModel olxAdEntityRelationalModel = new OlxAdEntityRelationalModel();
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(HistoricalOfOlxAds.class.getName());

        try (ResultSet allAdsResultSet = olxAdEntityRelationalModel.selectAllAdLinks()) {
            while (allAdsResultSet.next()) {

            }
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }
}
