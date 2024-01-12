package com.fuse.sql.constants;

public interface AliexpressAds {
    // xpath constants
    // Click on after
    String specificationsSeeMoreButtonCssSelector = "button.comet-v2-btn:nth-child(3)";
    String titleCssSelector = ".title--wrap--Ms9Zv4A > h1:nth-child(1)";
    String oldPriceCssSelector = ".price--originalText--Zsc6sMv";
    String productVariationsCssSelector = ".sku-item--skus--MmsF8fD";
    String priceCssSelector = ".price--current--H7sGzqb";
    String discountPercentCssSelector = ".price--discount--xET8qnP";
    String estimatedTaxCssSelector = "a.vat-installment--item--XEHEcM1";
    String valueInInstallmentsOrNationalProductCssSelector = "span.vat-installment--item--XEHEcM1";
    String quantitySoldCssSelector = ".reviewer--wrap--sPGWrNq > span:nth-child(5)";
    String shippingCssSelector = "div.dynamic-shipping-line:nth-child(1) > span:nth-child(1) > span:nth-child(1) > strong:nth-child(1)";
    String choiceBannerCssSelector = ".banner-choice--wrap--eH19LKI";
    String estimatedDeliveryDateCssSelectorForNonChoiceProducts = "div.dynamic-shipping-line:nth-child(3) > span:nth-child(1) > span:nth-child(2)";
    String estimatedDeliveryDateCssSelectorForChoiceProducts = "div.dynamic-shipping-line:nth-child(2) > span:nth-child(1) > span:nth-child(2) > strong:nth-child(1)";

    String createTableAliAdsQuery = """
            CREATE TABLE IF NOT EXISTS ali_ads (
              sku_id bigint PRIMARY KEY,
              link varchar(150) UNIQUE NOT NULL,
              collect_timestamp timestamp NOT NULL,
              title varchar(150) NOT NULL,
              old_price double precision,
              price double precision,
              discount_percent int2,
              installments_number int2,
              installments_value double precision,
              estimated_tax_value double precision,
              quantity_sold int4,
              national_product bool,
              shipping_cost double precision,
              is_choice bool,
              estimated_delivery_in_days int2
            );
            """;

    String selectSpecificAdFromAliAdsQuery = """
            SELECT
              sku_id,
              link,
              collect_timestamp,
              title,
              old_price,
              price,
              discount_percent,
              installments_number,
              installments_value,
              quantity_sold,
              national_product,
              shipping_cost,
              is_choice,
              estimated_delivery_in_days
            FROM
              ali_ads
            WHERE
              sku_id = ?
            """;

    String selectAllAdsFromAliAdsQuery = """
            SELECT
              sku_id,
              link,
              collect_timestamp,
              title,
              old_price,
              price,
              discount_percent,
              installments_number,
              installments_value,
              quantity_sold,
              national_product,
              shipping_cost,
              is_choice,
              estimated_delivery_in_days
            FROM
              ali_ads
            """;

    String deleteSpecificAdFromAliAdsQuery = """
            DELETE FROM ali_ads WHERE sku_id = ?
            """;

    String insertAdIntoAliAdsQuery = """
            INSERT INTO ali_ads (sku_id, link, collect_timestamp, title, old_price, price, discount_percent,
              installments_number, installments_value, estimated_tax_value, quantity_sold, national_product,
              shipping_cost, is_choice, estimated_delivery_in_days) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
}
