package com.fuse.sql.models;

import org.postgresql.util.PGobject;

import java.sql.Array;
import java.sql.Timestamp;

public class OlxAdModel {
    public long skuId;
    public String link;
    public Timestamp collectTimestamp;
    public PGobject json;
    public String title;
    public String description;
    public Double price;
    public Array images;
    public String seller;
    public String category;
    public String subcategory;
    public Long cep;
    public String city;
    public String neighbourhood;
    public PGobject details;
}
