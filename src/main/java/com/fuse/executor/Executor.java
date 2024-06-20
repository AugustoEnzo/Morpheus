package com.fuse.executor;

import com.fuse.crawlers.HistoricalOfOlxAds;
import com.fuse.crawlers.OlxAds;
import com.fuse.crawlers.OlxAdsLinks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Executor {
    private static final Logger logger = Logger.getLogger(Executor.class.getName());
    public static void main(String[] args) {
        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        final ExecutorService executorService = Executors.newFixedThreadPool(3);

        // Execute crawling of olx ads links to the database
        scheduledExecutorService.scheduleAtFixedRate(new Thread(new OlxAdsLinks()), 0, 1, TimeUnit.HOURS);

        // Execute crawling of olx ads to the database
        executorService.submit(new Thread(new OlxAds()));

        // Execute the crawling olx ads historical to the database
        executorService.submit(new Thread(new HistoricalOfOlxAds()));

        try {
            boolean termination = executorService.awaitTermination(2, TimeUnit.HOURS);
            if (termination) {
                scheduledExecutorService.shutdown();
                executorService.shutdown();
            }
        } catch (InterruptedException interruptedException) {
            logger.severe(String.format("We got an interruption at %s %s",
                    ExecutorService.class.getName(), interruptedException
            ));
        }
    }
}
