package com.fuse.sql.erm;

import com.fuse.sql.connection.PostgresJDBCDriverConnector;
import com.fuse.sql.constants.OlxAdsLinks;
import com.fuse.sql.models.OlxAdLinkModel;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class OlxAdLinkEntityRelationalModel implements OlxAdsLinks {
    Logger logger = Logger.getLogger(OlxAdLinkEntityRelationalModel.class.getName());
    Connection conn = new PostgresJDBCDriverConnector().conn;

    public void createOlxAdLinkTable() {
        try {
            Statement statement = conn.createStatement();
            statement.execute(createTableOlxAdsLinks);
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }

    public Set<OlxAdLinkModel> selectSpecificAd(long skuId) {
        Set<OlxAdLinkModel> result = new HashSet<>();

        try {
            PreparedStatement statement = conn.prepareStatement(selectSpecificAdFromOlxAdLinkQuery);
            statement.setLong(1, skuId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                OlxAdLinkModel olxAdLinkModel = new OlxAdLinkModel();
                olxAdLinkModel.skuId = resultSet.getLong(1);
                olxAdLinkModel.link = resultSet.getString(2);
                olxAdLinkModel.collectTimestamp = resultSet.getTimestamp(3);

                result.add(olxAdLinkModel);
            }

            return result;
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
        return result;
    }

    public ResultSet selectAllAdLinks() {
        try {
            PreparedStatement statement = conn.prepareStatement(selectAllSkusFromOlxAdsLinkQuery);
            statement.setFetchSize(0);
            return statement.executeQuery();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
        return null;
    }

    public void deleteSpecificAd(long skuId) {
        try {
            PreparedStatement statement = conn.prepareStatement(deleteSpecificSkusFromOlxAdsLinkQuery);
            statement.setLong(1, skuId);
            int result = statement.executeUpdate();

            if (result == 1) {
                logger.info(skuId + "was successfully deleted from olx_ads_links");
            } else {
                logger.info(skuId + "wasn't deleted from olx_ads_links");
            }

            statement.close();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }

    public void insertNewAd(OlxAdLinkModel adLinkModel) {
        try {
            PreparedStatement statement = conn.prepareStatement(insertSkuIntoOlxAdsLinkQuery);
            statement.setLong(1, adLinkModel.skuId);
            statement.setString(2, adLinkModel.link);
            statement.setTimestamp(3, adLinkModel.collectTimestamp);

            int result = statement.executeUpdate();

            if (result == 1) {
                logger.info(String.format("Sku: %d was successfully inserted at olx_ads_link table", adLinkModel.skuId));
            } else {
                logger.severe(String.format("Sku: %d wasn't inserted at olx_ads_link", adLinkModel.skuId));
            }

            statement.close();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }

    public void updateSpecificSku(OlxAdLinkModel adLinkModel) {
        try {
            PreparedStatement statement = conn.prepareStatement(updateSpecificSkuIntoOlxAdsLinkQuery);
            statement.setString(1, adLinkModel.link);
            statement.setTimestamp(2, adLinkModel.collectTimestamp);
            statement.setLong(3, adLinkModel.skuId);
            int result = statement.executeUpdate();

            if (result == 1) {
                logger.info(String.format("Sku: %d was successfully updated at olx_ads_link table with ad link: %s", adLinkModel.skuId, adLinkModel.link));
            } else {
                logger.severe(String.format("Sku: %d wasn't updated at olx_ads_link", adLinkModel.skuId));
            }

            statement.close();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }
}
