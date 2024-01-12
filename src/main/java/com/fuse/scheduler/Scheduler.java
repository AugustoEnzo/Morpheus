package com.fuse.scheduler;

import com.fuse.crawlers.AliexpressAds;
import com.fuse.crawlers.AliexpressAdsLinks;
import com.fuse.crawlers.OlxAdsLinks;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    public static void main(String[] args) {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

        // Execute crawling to insert aliexpress ads in the database
        scheduler.scheduleAtFixedRate(new Thread(new AliexpressAds()), 0, 2, TimeUnit.HOURS);

        // Execute crawling of aliexpress ads links in the database
        scheduler.scheduleAtFixedRate(new Thread(new AliexpressAdsLinks()), 0, 1, TimeUnit.HOURS);

        // Execute crawling of olx ads links to the database
        scheduler.scheduleAtFixedRate(new Thread(new OlxAdsLinks()), 0, 1, TimeUnit.HOURS);
    }
}
