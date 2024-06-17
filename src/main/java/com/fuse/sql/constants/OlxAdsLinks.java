package com.fuse.sql.constants;

public interface OlxAdsLinks {
    String olxUrlBeginConstant = "https://www.olx.com.br/";
    String olxUrlEndConstant = "/estado-am?o=1";
    int olxLastPage = 100;
    int crawlingTimeout = 3;
    String listOfAdsCssSelector = ".sc-8be2c8c5-2";
    String listOfAdsXPath = "/html/body/div/div/main/div/div[2]/main/div[4]";
    String createTableOlxAdsLinks = """
            CREATE TABLE IF NOT EXISTS olx_ads_link (
              sku_id bigint PRIMARY KEY,
              ad_link varchar(300) UNIQUE NOT NULL,
              collect_timestamp timestamp NOT NULL
            );
            """;

    String selectSpecificAdFromOlxAdLinkQuery = """
            SELECT
              sku_id,
              ad_link,
              collect_timestamp
            FROM
              olx_ads_link
            WHERE
              sku_id = ?
            """;

    String selectAllSkusFromOlxAdsLinkQuery = """
            SELECT
              sku_id,
              ad_link,
              collect_timestamp
            FROM
              olx_ads_link
            """;

    String deleteSpecificSkusFromOlxAdsLinkQuery = """
            DELETE FROM olx_ads_link WHERE sku_id = ?;
            """;

    String insertSkuIntoOlxAdsLinkQuery = """
            INSERT INTO olx_ads_link VALUES (?,?,?);
            """;

    String updateSpecificSkuIntoOlxAdsLinkQuery = """
            UPDATE ali_ads_link
            SET ad_link = ?,
            SET collect_timestamp = ?
            WHERE
              sku_id = ?;
            """;
}
