package com.fuse.scheduler;

import com.fuse.crawlers.AliexpressAds;
import com.fuse.crawlers.AliexpressAdsLinks;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    public static void main(String[] args) {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

        // Execute crawling to insert ads in the database
        scheduler.scheduleAtFixedRate(new Thread(new AliexpressAds()), 0, 8, TimeUnit.HOURS);

        // Execute crawling of the ads in the database
        scheduler.scheduleAtFixedRate(new Thread(new AliexpressAdsLinks()), 0, 1, TimeUnit.HOURS);
    }
}
