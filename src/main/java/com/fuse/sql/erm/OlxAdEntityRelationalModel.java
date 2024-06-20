package com.fuse.sql.erm;

import com.fuse.sql.connection.PostgresJDBCDriverConnector;
import com.fuse.sql.constants.OlxAds;
import com.fuse.sql.models.OlxAdModel;
import org.postgresql.util.PGobject;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class OlxAdEntityRelationalModel implements OlxAds {
    Logger logger = Logger.getLogger(OlxAdEntityRelationalModel.class.getName());
    Connection conn = new PostgresJDBCDriverConnector().conn;

    public void createTable() {
        try {
            Statement statement = conn.createStatement();
            statement.execute(createTableOlxAdsQuery);
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }

    public ResultSet selectAllAdLinks() {
        try {
            PreparedStatement statement = conn.prepareStatement(selectAllAdsFromOlxAdsQuery);
            statement.setFetchSize(0);
            return statement.executeQuery();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
        return null;
    }

    public void deleteSpecificAd(long skuId) {
        try {
            PreparedStatement statement = conn.prepareStatement(deleteSpecificAdFromOlxAdsQuery);
            statement.setLong(1, skuId);
            int result = statement.executeUpdate();

            if (result == 1) {
                logger.info(skuId + " was successfully deleted from olx_ads");
            } else {
                logger.info(skuId + " wasn't deleted from olx_ads");
            }

            statement.close();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }

    public Set<OlxAdModel> selectSpecificAd(long skuId) {
        Set<OlxAdModel> result = new HashSet<>();

        try {
            PreparedStatement statement = conn.prepareStatement(selectSpecificAdFromOlxAdsQuery);
            statement.setLong(1, skuId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                OlxAdModel olxAdModel = new OlxAdModel();
                olxAdModel.skuId = resultSet.getLong(1);
                olxAdModel.link = resultSet.getString(2);
                olxAdModel.collectTimestamp = resultSet.getTimestamp(3);
                olxAdModel.json = resultSet.getObject(4, PGobject.class);
                olxAdModel.title = resultSet.getString(5);
                try {
                    olxAdModel.description = resultSet.getString(6);
                } catch (PSQLException psqlException) {
                    olxAdModel.description = null;
                }

                try {
                    olxAdModel.price = resultSet.getDouble(7);
                } catch (PSQLException psqlException) {
                    olxAdModel.price = null;
                }

                try {
                    olxAdModel.images = resultSet.getArray(8);
                } catch (PSQLException psqlException) {
                    olxAdModel.images = null;
                }

                try {
                    olxAdModel.seller = resultSet.getString(9);
                } catch (PSQLException psqlException) {
                    olxAdModel.seller = null;
                }

                try {
                    olxAdModel.category = resultSet.getString(10);
                } catch (PSQLException psqlException) {
                    olxAdModel.category = null;
                }

                try {
                    olxAdModel.subcategory = resultSet.getString(11);
                } catch (PSQLException psqlException) {
                    olxAdModel.subcategory = null;
                }

                try {
                    olxAdModel.cep = resultSet.getLong(12);
                } catch (PSQLException psqlException) {
                    olxAdModel.cep = null;
                }

                try {
                    olxAdModel.city = resultSet.getString(13);
                } catch (PSQLException psqlException) {
                    olxAdModel.city = null;
                }

                try {
                    olxAdModel.neighbourhood = resultSet.getString(14);
                } catch (PSQLException psqlException) {
                    olxAdModel.neighbourhood = null;
                }

                try {
                    olxAdModel.details = resultSet.getObject(15, PGobject.class);
                } catch (PSQLException psqlException) {
                    olxAdModel.details = null;
                }

                result.add(olxAdModel);
            }

            return result;
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
        return result;
    }

    public void insertNewAd(OlxAdModel olxAdModel) {
        try {
            PreparedStatement statement = conn.prepareStatement(insertAdIntoOlxAdsQuery);
            statement.setLong(1, olxAdModel.skuId);
            statement.setString(2, olxAdModel.link);
            statement.setTimestamp(3, olxAdModel.collectTimestamp);
            statement.setObject(4, olxAdModel.json);
            statement.setString(5, olxAdModel.title);

            if (olxAdModel.description != null) {
                statement.setString(6, olxAdModel.description);
            } else {
                statement.setNull(6, Types.VARCHAR);
            }

            if (olxAdModel.price != null) {
                statement.setDouble(7, olxAdModel.price);
            } else {
                statement.setNull(7, Types.DOUBLE);
            }

            if (olxAdModel.images != null) {
                statement.setArray(8, olxAdModel.images);
            } else {
                statement.setNull(8, Types.ARRAY);
            }

            if (olxAdModel.seller != null) {
                statement.setString(9, olxAdModel.seller);
            } else {
                statement.setNull(9, Types.VARCHAR);
            }

            if (olxAdModel.category != null) {
                statement.setString(10, olxAdModel.category);
            } else {
                statement.setNull(10, Types.VARCHAR);
            }

            if (olxAdModel.subcategory != null) {
                statement.setString(11, olxAdModel.subcategory);
            } else {
                statement.setNull(11, Types.VARCHAR);
            }

            if (olxAdModel.cep != null) {
                statement.setLong(12, olxAdModel.cep);
            } else {
                statement.setNull(12, Types.BIGINT);
            }

            if (olxAdModel.city != null) {
                statement.setString(13, olxAdModel.city);
            } else {
                statement.setNull(13, Types.VARCHAR);
            }

            if (olxAdModel.neighbourhood != null) {
                statement.setString(14, olxAdModel.neighbourhood);
            } else {
                statement.setNull(14, Types.VARCHAR);
            }

            if (olxAdModel.details != null) {
                statement.setObject(15, olxAdModel.details);
            } else {
                statement.setNull(15, Types.STRUCT);
            }

            int result = statement.executeUpdate();

            if (result > 0) {
                logger.info(String.format("Sku: %d was successfully inserted at olx_ads table", olxAdModel.skuId));
            } else {
                logger.severe(String.format("Sku: %d wasn't inserted at olx_ads", olxAdModel.skuId));
            }

            statement.close();
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }

    public Array createArrayOf(ArrayList<Object> arrayOfValues, String sqlType) throws SQLException {
        return conn.createArrayOf(sqlType, arrayOfValues.toArray());
    }


}
