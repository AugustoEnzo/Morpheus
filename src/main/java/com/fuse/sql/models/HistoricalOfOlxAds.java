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
    public Array newImagesArray;
    public Boolean offline;
}
