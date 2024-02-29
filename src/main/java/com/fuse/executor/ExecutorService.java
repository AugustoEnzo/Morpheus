package com.fuse.executor;

import com.fuse.crawlers.OlxAds;
import com.fuse.crawlers.OlxAdsLinks;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ExecutorService {
    private static final Logger logger = Logger.getLogger(ExecutorService.class.getName());
    public static void main(String[] args) {
        final java.util.concurrent.ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Execute crawling of olx ads links to the database
        executorService.submit(new Thread(new OlxAdsLinks()));
        // Execute crawling of olx ads to the database
        executorService.submit(new Thread(new OlxAds()));
        try {
            boolean termination = executorService.awaitTermination(1, TimeUnit.HOURS);
            if (termination) {
                executorService.shutdown();
            }
        } catch (InterruptedException interruptedException) {
            logger.severe(String.format("We got an interruption at %s %s",
                    ExecutorService.class.getName(), interruptedException
            ));
        }
    }
}
