package com.fuse.sql.constants;

public interface HistoricalOfOlxAds {
    int crawlingTimeout = 3;
    String createTableHistoricalOlxAdsQuery = """
            CREATE TABLE IF NOT EXISTS historical_of_olx_ads (
                id serial PRIMARY KEY,
                sku_id bigint NOT NULL,
                link varchar(300) NOT NULL,
                collect_timestamp timestamp NOT NULL,
                new_price double precision,
                new_json JSON,
                new_images TEXT[],
                offline boolean
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
                offline
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
                offline
            FROM
                historical_of_olx_ads;
            """;

    String deleteSpecificChangeFromHistoricalOlxAdsQuery = """
            DELETE FROM historical_of_olx_ads WHERE id = ? AND sku_id = ?;
            """;

    String insertChangeIntoHistoricalOlxAdsQuery = """
            INSERT INTO historical_of_olx_ads VALUES (?, ?, ?, ?, ?, ?, ?);
            """;
}
