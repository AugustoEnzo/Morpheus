package com.fuse.crawlers;

import com.fuse.sql.erm.OlxAdLinkEntityRelationalModel;
import com.fuse.sql.models.OlxAdLinkModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.fuse.sql.helpers.CrawlerHelper;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OlxAdsLinks implements com.fuse.sql.constants.OlxAdsLinks, Runnable {
//public class OlxAdsLinks implements com.fuse.sql.constants.OlxAdsLinks {
    static CrawlerHelper crawlerHelper = new CrawlerHelper();
    static OlxAdLinkEntityRelationalModel olxAdLinkEntityRelationalModel = new OlxAdLinkEntityRelationalModel();
    static Pattern skuIdPattern = Pattern.compile("[0-9]{4,25}$", Pattern.CASE_INSENSITIVE);
    public void run() {
        Logger logger = Logger.getLogger(OlxAdsLinks.class.getName());

        Set<String> setOfCategories = new HashSet<>();
        setOfCategories.add("eletronicos-e-celulares");
        setOfCategories.add("informatica");
        setOfCategories.add("tvs-e-video");

        for (String category : setOfCategories) {
            logger.info("Crawling OLX data for category: " + category);
            olxAdLinkEntityRelationalModel.createTable();
            for (int page = 1; page < olxLastPage; page++) {
                int pageScrappingTries = 0;
                while (pageScrappingTries < 1) {
                    String olxUrl = String.format("%s%s%s", olxUrlBeginConstant, category, olxUrlEndConstant)
                            .replace("o=1", String.format("o=%s", page));
                    try {
                        Document pageDocument = Jsoup.connect(olxUrl)
                                .data("query", "link")
                                .userAgent("Mozilla")
                                .timeout(3000)
                                .get();

                        // Redundancy essential for crawler
                        Elements listOfAds = crawlerHelper.tryToGetValuesUsingCssSelectorOrXpathJsoupBased(
                                "list of ads",
                                pageDocument,
                                listOfAdsCssSelector,
                                listOfAdsXPath
                        );

                        for (Element adElement : listOfAds) {
                            OlxAdLinkModel adLinkModel = new OlxAdLinkModel();

                            adLinkModel.link = adElement.select("div > section > a[href]").attr("href");

                            Matcher matcher = skuIdPattern.matcher(adLinkModel.link);

                            if (matcher.find()) {
                                adLinkModel.skuId = Long.parseLong(matcher.group(0));
                            }

                            adLinkModel.collectTimestamp = Timestamp.from(Instant.now());

                            Set<OlxAdLinkModel> specificAdResult =
                                    olxAdLinkEntityRelationalModel.selectSpecificAd(adLinkModel.skuId);

                            if (specificAdResult.isEmpty()) {
                                olxAdLinkEntityRelationalModel.insertNewAd(adLinkModel);
                            } else if (specificAdResult.stream().findFirst().get().skuId != adLinkModel.skuId && !Objects.equals(specificAdResult.stream().findFirst().get().link, adLinkModel.link)) {
                                olxAdLinkEntityRelationalModel.updateSpecificSku(adLinkModel);
                            }
                        }
                    } catch (IOException exception) {
                        logger.severe(exception.toString());
                    }
                    pageScrappingTries += 1;
                }
            }
        }
        logger.fine("Finished querying for the wanted categories");
    }
}
