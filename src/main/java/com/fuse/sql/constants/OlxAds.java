package com.fuse.sql.constants;

public interface OlxAds {
    String adJsonTypeValue = "application/ld+json";
    String sellerMainCssSelector = ".ad__sc-ypp2u2-4";
    String sellerSecondaryCssSelector = ".sc-fBuWsC";
    String categoryMainCssSelector = "div.ad__sc-2h9gkk-0:nth-child(1) > div:nth-child(1) > a:nth-child(2)";
    String locationMainCssSelector = "#location > div:nth-child(1)";
    String locationSecondaryCssSelector = ".ckFhRN > div:nth-child(1)";
    String detailsMainCssSelector = "#details > div:nth-child(1)";
    String detailsSecondaryCssSelector = "div.ad__sc-h3us20-6:nth-child(29) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1)";
    String getInOlxSinceMainCssSelector = "div.ad__sc-k5plwo-2:nth-child(1) > div:nth-child(1) > span:nth-child(2)";
    String inOlxSinceSecondaryCssSelector = ".sc-gNJABI";
    String publicationDateMainCssSelector = ".ad__sc-1rv3ob5-0";
    String publicationDateSecondaryCssSelector = "div.ad__sc-h3us20-6:nth-child(33) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > span:nth-child(1)";
    String imagesArraySQLType = "TEXT";
    int crawlingTimeout = 3;
    String createTableOlxAdsQuery = """
            CREATE TABLE IF NOT EXISTS olx_ads (
              sku_id bigint PRIMARY KEY,
              link varchar(300) UNIQUE NOT NULL,
              collect_timestamp timestamp NOT NULL,
              json JSON NOT NULL,
              title varchar(300) NOT NULL,
              description text,
              price double precision,
              images TEXT[],
              seller varchar(100),
              category varchar(50),
              subcategory varchar(100),
              cep bigint,
              city varchar(150),
              neighbourhood varchar(100),
              details JSON
            );
            """;

    String selectSpecificAdFromOlxAdsQuery = """
            SELECT
              sku_id,
              link,
              collect_timestamp,
              json,
              title,
              description,
              price,
              images,
              seller,
              category,
              subcategory,
              cep,
              city,
              neighbourhood,
              details
            FROM
              olx_ads
            WHERE
              sku_id = ?
            """;

    String selectAllAdsFromOlxAdsQuery = """
            SELECT
              sku_id,
              link,
              collect_timestamp,
              json,
              title,
              description,
              price,
              images,
              seller,
              category,
              subcategory,
              cep,
              city,
              neighbourhood,
              details
            FROM
              olx_ads
            """;

    String deleteSpecificAdFromOlxAdsQuery = """
            DELETE FROM olx_ads WHERE sku_id = ?
            """;

    String insertAdIntoOlxAdsQuery = """
            INSERT INTO olx_ads VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
}
