package com.fuse.sql.erm;

import com.fuse.sql.connection.PostgresJDBCDriverConnector;
import com.fuse.sql.constants.AliexpressAdsLinks;
import com.fuse.sql.models.AliexpressAdLinkModel;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class AliexpressAdLinkEntityRelationalModel implements AliexpressAdsLinks {
    Logger logger = Logger.getLogger(AliexpressAdLinkEntityRelationalModel.class.getName());
    Connection conn = new PostgresJDBCDriverConnector().conn;

    public void createAliAdsLinksTable() {
        try {
            Statement statement = conn.createStatement();
            statement.execute(createTableAliAdsLinkQuery);
            logger.fine(AliexpressAdLinkEntityRelationalModel.class.getName() + " could create the topics without more problems");
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }

    public Set<AliexpressAdLinkModel> selectSpecificAd(long skuId) {
        Set<AliexpressAdLinkModel> result = new HashSet<>();
        try {
            PreparedStatement statement = conn.prepareStatement(selectSpecificAdFromAliAdsLinkQuery);
            statement.setLong(1, skuId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                AliexpressAdLinkModel adLinkModel = new AliexpressAdLinkModel();
                adLinkModel.skuId = resultSet.getLong(1);
                adLinkModel.link = resultSet.getString(2);
                adLinkModel.collectTimestamp = resultSet.getTimestamp(3);

                result.add(adLinkModel);
            }

            return result;
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
        return result;
    }

    public ResultSet selectAllAdLinks() {
        try {
            PreparedStatement statement = conn.prepareStatement(selectAllSkusFromAliAdsLinkQuery);
            statement.setFetchSize(0);
            return statement.executeQuery();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
        return null;
    }

    public void deleteSpecificAd(long skuId) {
        try {
         PreparedStatement statement = conn.prepareStatement(deleteSpecificSkusFromAliAdsLinkQuery);
         statement.setLong(1, skuId);
         int result = statement.executeUpdate();

         if (result == 1) {
             logger.info(skuId + "was successfully deleted from ali_ads_links");
         } else {
             logger.info(skuId + "wasn't deleted from ali_ads_links");
         }

         statement.close();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }

    public void insertNewAd(AliexpressAdLinkModel adLinkModel) {
        try {
            PreparedStatement statement = conn.prepareStatement(insertSkuIntoAliAdsLinkQuery);
            statement.setLong(1, adLinkModel.skuId);
            statement.setString(2, adLinkModel.link);
            statement.setTimestamp(3, adLinkModel.collectTimestamp);

            int result = statement.executeUpdate();

            if (result == 1) {
                logger.info(String.format("Sku: %d was successfully inserted at ali_ads_link table", adLinkModel.skuId));
            } else {
                logger.severe(String.format("Sku: %d wasn't inserted at ali_ads_link", adLinkModel.skuId));
            }

            statement.close();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }

    public void updateSpecificSku(AliexpressAdLinkModel adLinkModel) {
        try {
         PreparedStatement statement = conn.prepareStatement(updateSpecificSkuIntoAliAdsLinkQuery);
         statement.setString(1, adLinkModel.link);
         statement.setTimestamp(2, adLinkModel.collectTimestamp);
         statement.setLong(3, adLinkModel.skuId);
         int result = statement.executeUpdate();

         if (result == 1) {
             logger.info(String.format("Sku: %d was successfully updated at ali_ads_link table with ad link: %s", adLinkModel.skuId, adLinkModel.link));
         } else {
             logger.severe(String.format("Sku: %d wasn't updated at ali_ads_link", adLinkModel.skuId));
         }

         statement.close();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }
}
