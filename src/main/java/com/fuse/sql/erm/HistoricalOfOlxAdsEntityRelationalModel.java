package com.fuse.sql.erm;

import com.fuse.sql.connection.PostgresJDBCDriverConnector;
import com.fuse.sql.constants.HistoricalOfOlxAds;
import org.postgresql.util.PGobject;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;
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

            if (result > 0) {
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
                try {
                    historicalOfOlxAdsModel.newPrice = resultSet.getDouble(5);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.newPrice = null;
                }
                try {
                    historicalOfOlxAdsModel.newJson = resultSet.getObject(6, PGobject.class);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.newJson = null;
                }
                try {
                    historicalOfOlxAdsModel.newImages = resultSet.getArray(7);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.newImages = null;
                }
                try {
                    historicalOfOlxAdsModel.offline = resultSet.getBoolean(8);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.offline = null;
                }
                try {
                    historicalOfOlxAdsModel.oldPrice = resultSet.getDouble(9);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.oldPrice = null;
                }
                try {
                    historicalOfOlxAdsModel.oldJson = resultSet.getObject(10, PGobject.class);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.oldJson = null;
                }
                try {
                    historicalOfOlxAdsModel.oldImages = resultSet.getArray(11);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.oldImages = null;
                }
                try {
                    historicalOfOlxAdsModel.title = resultSet.getString(12);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.title = null;
                }
                try {
                    historicalOfOlxAdsModel.description = resultSet.getString(13);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.description = null;
                }
                try {
                    historicalOfOlxAdsModel.seller = resultSet.getString(14);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.seller = null;
                }
                try {
                    historicalOfOlxAdsModel.category = resultSet.getString(15);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.category = null;
                }
                try {
                    historicalOfOlxAdsModel.subcategory = resultSet.getString(16);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.subcategory = null;
                }
                try {
                    historicalOfOlxAdsModel.cep = resultSet.getLong(17);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.cep = null;
                }
                try {
                    historicalOfOlxAdsModel.city = resultSet.getString(18);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.city = null;
                }
                try {
                    historicalOfOlxAdsModel.neighbourhood = resultSet.getString(19);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.neighbourhood = null;
                }
                try {
                    historicalOfOlxAdsModel.details = resultSet.getObject(20, PGobject.class);
                } catch (PSQLException psqlException) {
                    historicalOfOlxAdsModel.details = null;
                }

                result.add(historicalOfOlxAdsModel);
            }
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
        return result;
    }

    public void insertNewAd(com.fuse.sql.models.HistoricalOfOlxAds historicalOfOlxAds) {
        try {
            PreparedStatement statement = conn.prepareStatement(insertChangeIntoHistoricalOlxAdsQuery);
            statement.setLong(1, historicalOfOlxAds.skuId);
            statement.setString(2, historicalOfOlxAds.link);
            statement.setTimestamp(3, historicalOfOlxAds.collectTimestamp);

            if (historicalOfOlxAds.newPrice != null) {
                statement.setDouble(4, historicalOfOlxAds.newPrice);
            } else {
                statement.setNull(4, Types.DOUBLE);
            }

            if (historicalOfOlxAds.newJson != null) {
                statement.setObject(5, historicalOfOlxAds.newJson);
            } else {
                statement.setNull(5, Types.STRUCT);
            }

            if (historicalOfOlxAds.newImages != null) {
                statement.setArray(6, historicalOfOlxAds.newImages);
            } else {
                statement.setNull(6, Types.ARRAY);
            }

            if (historicalOfOlxAds.offline != null) {
                statement.setBoolean(7, historicalOfOlxAds.offline);
            } else {
                statement.setNull(7, Types.BOOLEAN);
            }

            if (historicalOfOlxAds.oldPrice != null) {
                statement.setDouble(8, historicalOfOlxAds.oldPrice);
            } else {
                statement.setNull(8, Types.DOUBLE);
            }

            if (historicalOfOlxAds.oldJson != null) {
                statement.setObject(9, historicalOfOlxAds.oldJson);
            } else {
                statement.setNull(9, Types.STRUCT);
            }

            if (historicalOfOlxAds.oldImages != null) {
                statement.setArray(10, historicalOfOlxAds.oldImages);
            } else {
                statement.setNull(10, Types.ARRAY);
            }

            if (historicalOfOlxAds.title != null) {
                statement.setString(11, historicalOfOlxAds.title);
            } else {
                statement.setNull(11, Types.VARCHAR);
            }

            if (historicalOfOlxAds.description != null) {
                statement.setString(12, historicalOfOlxAds.description);
            } else {
                statement.setNull(12, Types.VARCHAR);
            }

            if (historicalOfOlxAds.seller != null) {
                statement.setString(13, historicalOfOlxAds.seller);
            } else {
                statement.setNull(13, Types.VARCHAR);
            }

            if (historicalOfOlxAds.category != null) {
                statement.setString(14, historicalOfOlxAds.category);
            } else {
                statement.setNull(14, Types.VARCHAR);
            }

            if (historicalOfOlxAds.subcategory != null) {
                statement.setString(15, historicalOfOlxAds.subcategory);
            } else {
                statement.setNull(15, Types.VARCHAR);
            }

            if (historicalOfOlxAds.cep != null) {
                statement.setLong(16, historicalOfOlxAds.cep);
            } else {
                statement.setNull(16, Types.BIGINT);
            }

            if (historicalOfOlxAds.city != null) {
                statement.setString(17, historicalOfOlxAds.city);
            } else {
                statement.setNull(17, Types.VARCHAR);
            }

            if (historicalOfOlxAds.neighbourhood != null) {
                statement.setString(18, historicalOfOlxAds.neighbourhood);
            } else {
                statement.setNull(18, Types.VARCHAR);
            }

            if (historicalOfOlxAds.details != null) {
                statement.setObject(19, historicalOfOlxAds.details);
            } else {
                statement.setNull(19, Types.STRUCT);
            }

            int result = statement.executeUpdate();

            if (result > 0) {
                logger.info(String.format("Sku: %d was successfully inserted at historical_of_olx_ads table", historicalOfOlxAds.skuId));
            } else {
                logger.info(String.format("Sku: %d wasn't inserted at historical_of_olx_ads", historicalOfOlxAds.skuId));
            }
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }

    public Array createArrayOf(ArrayList<Object> arrayOfValues, String sqlType) throws SQLException {
        return conn.createArrayOf(sqlType, arrayOfValues.toArray());
    }
}
