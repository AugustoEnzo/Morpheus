package com.fuse.scheduler;

import com.fuse.crawlers.OlxAds;
import com.fuse.crawlers.OlxAdsLinks;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    public static void main(String[] args) {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

        // Execute crawling of olx ads links to the database
        scheduler.scheduleAtFixedRate(new Thread(new OlxAdsLinks()), 0, 1, TimeUnit.HOURS);

        // Execute crawling of olx ads to the database
        scheduler.scheduleAtFixedRate(new Thread(new OlxAds()), 0, 1, TimeUnit.HOURS);
    }
}
