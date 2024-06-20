package com.fuse.sql.models;

import org.postgresql.util.PGobject;

import java.sql.Array;
import java.sql.Timestamp;

public class HistoricalOfOlxAds {
    public long skuId;
    public String link;
    public Timestamp collectTimestamp;
    public Double newPrice;
    public PGobject newJson;
    public Array newImages;
    public Boolean offline;
    public Double oldPrice;
    public PGobject oldJson;
    public Array oldImages;
    public String title;
    public String description;
    public String seller;
    public String category;
    public String subcategory;
    public Long cep;
    public String city;
    public String neighbourhood;
    public PGobject details;
}
