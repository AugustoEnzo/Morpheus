package com.fuse.sql.erm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class OlxAdEntityRelationalModel {
    static OlxAdLinkEntityRelationalModel olxAdLinkEntityRelationalModel = new OlxAdLinkEntityRelationalModel();
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(OlxAdLinkEntityRelationalModel.class.getName());

        try (ResultSet allAdsResultSet = olxAdLinkEntityRelationalModel.selectAllAdLinks()) {
            while (allAdsResultSet.next()) {

            }
        } catch (SQLException sqlException) {
            logger.severe(sqlException.toString());
        }
    }
}
