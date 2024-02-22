package com.fuse.sql.erm;

import com.fuse.sql.models.AliexpressAdModel;
import com.fuse.sql.connection.PostgresJDBCDriverConnector;
import com.fuse.sql.constants.AliexpressAds;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AliexpressAdEntityRelationalModel implements AliexpressAds {
    Logger logger = Logger.getLogger(AliexpressAdEntityRelationalModel.class.getName());
    Connection conn = new PostgresJDBCDriverConnector().conn;

    public void createAliAdsTable() {
        try {
            PreparedStatement statement = conn.prepareStatement(createTableAliAdsQuery);
            statement.executeQuery();
            logger.fine(AliexpressAdEntityRelationalModel.class.getName() + "");
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }

    public List<AliexpressAdModel> selectSpecificAd(long skuId) {
        List<AliexpressAdModel> result = new ArrayList<>();
        try {
            PreparedStatement statement = conn.prepareStatement(selectSpecificAdFromAliAdsQuery);
            statement.setLong(1, skuId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                AliexpressAdModel aliexpressAdModel = new AliexpressAdModel();
                aliexpressAdModel.skuId = resultSet.getLong(1);
                aliexpressAdModel.link = resultSet.getString(2);
                aliexpressAdModel.collectTimestamp = resultSet.getTimestamp(3);
                aliexpressAdModel.title = resultSet.getString(4);

                try {
                    resultSet.getDouble(5);
                    aliexpressAdModel.oldPrice = resultSet.getDouble(5);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.oldPrice = null;
                }

                try {
                    resultSet.getDouble(6);
                    aliexpressAdModel.price = resultSet.getDouble(6);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.price = null;
                }

                try {
                    resultSet.getInt(7);
                    aliexpressAdModel.discountPercent = resultSet.getInt(7);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.discountPercent = null;
                }

                try {
                    resultSet.getInt(8);
                    aliexpressAdModel.installmentsNumber = resultSet.getInt(8);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.installmentsNumber = null;
                }

                try {
                    resultSet.getDouble(9);
                    aliexpressAdModel.installmentsValue = resultSet.getDouble(9);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.installmentsValue = null;
                }

                try {
                    resultSet.getDouble(10);
                    aliexpressAdModel.estimatedTaxValue = resultSet.getDouble(10);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.estimatedTaxValue = null;
                }

                try {
                    resultSet.getInt(11);
                    aliexpressAdModel.quantitySold = resultSet.getInt(11);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.quantitySold = null;
                }

                try {
                    resultSet.getBoolean(12);
                    aliexpressAdModel.nationalProduct = resultSet.getBoolean(12);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.nationalProduct = null;
                }

                try {
                    resultSet.getDouble(13);
                    aliexpressAdModel.shippingCost = resultSet.getDouble(13);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.shippingCost = null;
                }

                try {
                    resultSet.getBoolean(14);
                    aliexpressAdModel.isChoice = resultSet.getBoolean(14);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.isChoice = null;
                }

                try {
                    resultSet.getLong(15);
                    aliexpressAdModel.estimatedDeliveryInDays = resultSet.getLong(15);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.estimatedDeliveryInDays = null;
                }

                result.add(aliexpressAdModel);
            }

            return result;
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }

        return result;
    }

    public List<AliexpressAdModel> selectAllAdsInNormalOrder() {
        List<AliexpressAdModel> result = new ArrayList<>();
        try {
            PreparedStatement statement = conn.prepareStatement(selectAllAdsFromAliAdsQuery);
            statement.setFetchSize(0);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                AliexpressAdModel aliexpressAdModel = new AliexpressAdModel();
                aliexpressAdModel.skuId = resultSet.getLong(1);
                aliexpressAdModel.link = resultSet.getString(2);
                aliexpressAdModel.collectTimestamp = resultSet.getTimestamp(3);
                aliexpressAdModel.title = resultSet.getString(4);

                try {
                    aliexpressAdModel.oldPrice = resultSet.getDouble(5);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.oldPrice = null;
                }

                try {
                    aliexpressAdModel.price = resultSet.getDouble(6);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.price = null;
                }

                try {
                    resultSet.getInt(7);
                    aliexpressAdModel.discountPercent = resultSet.getInt(7);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.discountPercent = null;
                }

                try {
                    resultSet.getInt(8);
                    aliexpressAdModel.installmentsNumber = resultSet.getInt(8);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.installmentsNumber = null;
                }

                try {
                    resultSet.getDouble(9);
                    aliexpressAdModel.installmentsValue = resultSet.getDouble(9);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.installmentsValue = null;
                }

                try {
                    resultSet.getDouble(10);
                    aliexpressAdModel.estimatedTaxValue = resultSet.getDouble(10);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.estimatedTaxValue = null;
                }

                try {
                    resultSet.getInt(11);
                    aliexpressAdModel.quantitySold = resultSet.getInt(11);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.quantitySold = null;
                }

                try {
                    resultSet.getBoolean(12);
                    aliexpressAdModel.nationalProduct = resultSet.getBoolean(12);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.nationalProduct = null;
                }

                try {
                    resultSet.getDouble(13);
                    aliexpressAdModel.shippingCost = resultSet.getDouble(13);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.shippingCost = null;
                }

                try {
                    resultSet.getBoolean(14);
                    aliexpressAdModel.isChoice = resultSet.getBoolean(14);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.isChoice = null;
                }

                try {
                    resultSet.getLong(15);
                    aliexpressAdModel.estimatedDeliveryInDays = resultSet.getLong(15);
                } catch (PSQLException psqlException) {
                    aliexpressAdModel.estimatedDeliveryInDays = null;
                }

                result.add(aliexpressAdModel);
            }

            return result;
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }

        return result;
    }

    public void deleteSpecificAd(long skuId) {
        try {
            PreparedStatement statement = conn.prepareStatement(deleteSpecificAdFromAliAdsQuery);
            statement.setLong(1, skuId);
            int result = statement.executeUpdate();

            if (result == 1) {
                logger.info(skuId + "was successfully deleted from ali_ads");
            } else {
                logger.info(skuId + "wasn't deleted from ali_ads");
            }

            statement.close();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }

    public void insertNewAd(AliexpressAdModel adModel) {
        try {
            PreparedStatement statement = conn.prepareStatement(insertAdIntoAliAdsQuery);
            statement.setLong(1, adModel.skuId);
            statement.setString(2, adModel.link);
            statement.setTimestamp(3, adModel.collectTimestamp);
            statement.setString(4, adModel.title);

            if (adModel.oldPrice != null) {
                statement.setDouble(5, adModel.oldPrice);
            } else {
                statement.setNull(5, Types.DOUBLE);
            }

            if (adModel.price != null) {
                statement.setDouble(6, adModel.price);
            } else {
                statement.setNull(6, Types.DOUBLE);
            }

            if (adModel.discountPercent != null) {
                statement.setInt(7, adModel.discountPercent);
            } else {
                statement.setNull(7, Types.INTEGER);
            }

            if (adModel.installmentsNumber != null) {
                statement.setInt(8, adModel.installmentsNumber);
            } else {
                statement.setNull(8, Types.INTEGER);
            }

            if (adModel.installmentsValue != null) {
                statement.setDouble(9, adModel.installmentsValue);
            } else {
                statement.setNull(9, Types.DOUBLE);
            }

            if (adModel.estimatedTaxValue != null) {
                statement.setDouble(10, adModel.estimatedTaxValue);
            } else {
                statement.setNull(10, Types.DOUBLE);
            }

            if (adModel.quantitySold != null) {
                statement.setInt(11, adModel.quantitySold);
            } else {
                statement.setNull(11, Types.INTEGER);
            }

            if (adModel.nationalProduct != null) {
                statement.setBoolean(12, adModel.nationalProduct);
            } else {
                statement.setNull(12, Types.BOOLEAN);
            }

            if (adModel.shippingCost != null) {
                statement.setDouble(13, adModel.shippingCost);
            } else {
                statement.setNull(13, Types.DOUBLE);
            }

            if (adModel.isChoice != null) {
                statement.setBoolean(14, adModel.isChoice);
            } else {
                statement.setNull(14, Types.BOOLEAN);
            }

            if (adModel.estimatedDeliveryInDays != null) {
                statement.setLong(15, adModel.estimatedDeliveryInDays);
            } else {
                statement.setNull(15, Types.BIGINT);
            }

            int result = statement.executeUpdate();

            if (result == 1) {
                logger.info(String.format("Sku: %d was successfully inserted at ali_ads table", adModel.skuId));
            } else {
                logger.severe(String.format("Sku: %d wasn't inserted at ali_ads", adModel.skuId));
            }

            statement.close();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }
}
