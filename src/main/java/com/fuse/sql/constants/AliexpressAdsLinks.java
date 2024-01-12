package com.fuse.sql.constants;

public interface AliexpressAdsLinks {
    int lastPageConstant = 100;
    int adsLimitPerPage = 60;

    // xpath constants
    String cardListDivXPath = "//*[@id=\"card-list\"]";
    String lastPageAnchorXPath = "/html/body/div[6]/div[1]/div/div[2]/div[3]/ul/li[8]/a";
    String createTableAliAdsLinkQuery = """
            CREATE TABLE IF NOT EXISTS ali_ads_link (
              sku_id bigint PRIMARY KEY,
              ad_link varchar(300) UNIQUE NOT NULL,
              collect_timestamp timestamp NOT NULL
            );
            """;

    String selectSpecificAdFromAliAdsLinkQuery = """
            SELECT
              sku_id,
              ad_link,
              collect_timestamp
            FROM
              ali_ads_link aal
            WHERE
              sku_id = ?
            """;

    String selectAllSkusFromAliAdsLinkQuery = """
            SELECT
              sku_id,
              ad_link,
              collect_timestamp
            FROM
              ali_ads_link
            """;

    String deleteSpecificSkusFromAliAdsLinkQuery = """
            DELETE FROM ali_ads_link WHERE sku_id = ?;
            """;

    String insertSkuIntoAliAdsLinkQuery = """
            INSERT INTO ali_ads_link (sku_id, ad_link, collect_timestamp) VALUES (?,?,?);
            """;

    String updateSpecificSkuIntoAliAdsLinkQuery = """
            UPDATE ali_ads_link
            SET ad_link = ?,
            SET collect_timestamp = ?
            WHERE
              sku_id = ?;
            """;

    String aliexpressQueryParams = """
                {"q":"PC+gaming","s":"qp_nw","osf":"category_navigate","sg_search_params":"on___( prism_tag_id:'1000342539' )","guide_trace":"d30aaf43-9a05-4d2a-babc-1f82ba4fbe02","scene_id":"32124","searchBizScene":"openSearch","recog_lang":"en","bizScene":"category_navigate","guideModule":"category_navigate_vertical","postCatIds":"7,21","scene":"category_navigate"}
                """;
}
