package com.fuse.sql.constants;

public interface OlxAdsLinks {
    String olxUrlBeginConstant = "https://www.olx.com.br/";
    String olxUrlEndConstant = "/estado-am?o=1";
    int olxLastPage = 100;

    String listOfAdsCssSelector = ".sc-e859a9d3-2";
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
            INSERT INTO olx_ads_link (sku_id, ad_link, collect_timestamp) VALUES (?,?,?);
            """;

    String updateSpecificSkuIntoOlxAdsLinkQuery = """
            UPDATE ali_ads_link
            SET ad_link = ?,
            SET collect_timestamp = ?
            WHERE
              sku_id = ?;
            """;
}
