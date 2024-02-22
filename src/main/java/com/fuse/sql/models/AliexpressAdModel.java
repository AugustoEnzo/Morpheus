package com.fuse.sql.models;

import java.sql.Timestamp;
import java.util.Map;

public class AliexpressAdModel {
    public long skuId;
    public String link;
    public Timestamp collectTimestamp;
    public String title;
    public Double oldPrice;
    public Double price;
    public Integer discountPercent;
    public Integer installmentsNumber;
    public Double installmentsValue;
    public Double estimatedTaxValue;
    public Integer quantitySold;
    public Boolean nationalProduct;
    public Double shippingCost;
    public Boolean isChoice;
    public Long estimatedDeliveryInDays;
    public Integer totalReviews;
    public Double averageReview;
    public Map<String, Object> productsVariations;
    public Map<String, Object> reviewIndicators;
    public Map<String, Object> specifications;
}
