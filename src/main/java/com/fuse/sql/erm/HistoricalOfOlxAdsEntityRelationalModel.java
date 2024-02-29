package com.fuse.sql.erm;

import com.fuse.sql.connection.PostgresJDBCDriverConnector;
import com.fuse.sql.constants.HistoricalOfOlxAds;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class HistoricalOfOlxAdsEntityRelationalModel implements HistoricalOfOlxAds {
    Logger logger = Logger.getLogger(HistoricalOfOlxAdsEntityRelationalModel.class.getName());
    Connection conn = new PostgresJDBCDriverConnector().conn;

    public void createTable() {
        try {
            Statement statement = conn.createStatement();
            statement.execute(createTableHistoricalOlxAdsQuery);
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }

    public ResultSet selectAllChanges() {
        try {
            PreparedStatement statement = conn.prepareStatement(selectAllChangesFromHistoricalOlxAdsQuery);
            statement.setFetchSize(0);
            return statement.executeQuery();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
        return null;
    }

    public void deleteSpecificChange(long id, long skuId) {
        try {
            PreparedStatement statement = conn.prepareStatement(deleteSpecificChangeFromHistoricalOlxAdsQuery);
            statement.setLong(1, id);
            statement.setLong(2, skuId);
            int result = statement.executeUpdate();

            if (result == 1) {
                logger.info(String.format("Change: %s of ad %s was successfully deleted from historical_of_olx_ads", id, skuId));
            } else {
                logger.info(String.format("Change: %s of ad %s was successfully deleted from historical_of_olx_ads", id, skuId));
            }

            statement.close();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }

    public Set<com.fuse.sql.models.HistoricalOfOlxAds> selectSpecificAd(long id, long skuId) {
        Set<com.fuse.sql.models.HistoricalOfOlxAds> result = new HashSet<>();

        try {
            PreparedStatement statement = conn.prepareStatement(selectSpecificChangesFromHistoricalOlxAdsQuery);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                com.fuse.sql.models.HistoricalOfOlxAds historicalOfOlxAdsModel = new com.fuse.sql.models.HistoricalOfOlxAds();
                historicalOfOlxAdsModel.skuId = resultSet.getLong(2);
                historicalOfOlxAdsModel.link = resultSet.getString(3);
                historicalOfOlxAdsModel.collectTimestamp = resultSet.getTimestamp(4);
            }
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
        return result;
    }
}


