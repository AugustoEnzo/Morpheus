package com.fuse.sql.constants;

public interface HistoricalOfOlxAds {
    String adJsonTypeValue = "application/ld+json";
    int crawlingTimeout = 3;
    String imagesArraySQLType = "TEXT";
    String createTableHistoricalOlxAdsQuery = """
            CREATE TABLE IF NOT EXISTS historical_of_olx_ads (
                id serial PRIMARY KEY,
                sku_id bigint NOT NULL,
                link varchar(300) NOT NULL,
                collect_timestamp timestamp NOT NULL,
                new_price double precision,
                new_json JSON,
                new_images TEXT[],
                offline boolean NOT NULL,
                old_price double precision,
                old_json JSON,
                old_images TEXT[],
                title varchar(300) NOT NULL,
                description text,
                seller varchar(300),
                category varchar(50),
                subcategory varchar(100),
                cep bigint,
                city varchar(150),
                neighbourhood varchar(100),
                details JSON
            );
            """;

    String selectSpecificChangesFromHistoricalOlxAdsQuery = """
            SELECT
                id,
                sku_id,
                link,
                collect_timestamp,
                new_price,
                new_json,
                new_images,
                offline,
                old_price,
                old_json,
                old_images,
                title,
                description,
                seller,
                category,
                subcategory,
                cep,
                city,
                neighbourhood,
                details
            FROM
                historical_of_olx_ads
            WHERE
                id = ?
                AND sku_id = ?;
            """;

    String selectAllChangesFromHistoricalOlxAdsQuery = """
            SELECT
                id,
                sku_id,
                link,
                collect_timestamp,
                new_price,
                new_json,
                new_images,
                offline,
                old_price,
                old_json,
                old_images,
                title,
                description,
                seller,
                category,
                subcategory,
                cep,
                city,
                neighbourhood,
                details
            FROM
                historical_of_olx_ads;
            """;

    String deleteSpecificChangeFromHistoricalOlxAdsQuery = """
            DELETE FROM historical_of_olx_ads WHERE id = ? AND sku_id = ?;
            """;

    String insertChangeIntoHistoricalOlxAdsQuery = """
            INSERT INTO\s
                historical_of_olx_ads (sku_id, link, collect_timestamp, new_price, new_json, new_images,
                offline, old_price, old_json, old_images, title, description, seller, category, subcategory,
                cep, city, neighbourhood, details)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
           \s""";
}
