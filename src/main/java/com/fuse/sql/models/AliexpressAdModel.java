package com.fuse.sql.models;

import java.sql.Timestamp;

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
}
