package com.fuse.crawlers;

import com.fuse.sql.erm.AliexpressAdEntityRelationalModel;
import com.fuse.sql.erm.AliexpressAdLinkEntityRelationalModel;
import com.fuse.helpers.CrawlerHelper;
import com.fuse.sql.models.AliexpressAdModel;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO Implements Runnable
public class AliexpressAds implements com.fuse.sql.constants.AliexpressAds {
    private static final Logger logger = Logger.getLogger(AliexpressAds.class.getName());
    private static final Pattern numericPattern = Pattern.compile("[0-9]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern estimatedDatePattern = Pattern.compile("[0-9]{2} [a-zA-Z]{3}", Pattern.CASE_INSENSITIVE);
    private static final Pattern percentPattern = Pattern.compile("[0-9]{1,3}", Pattern.CASE_INSENSITIVE);
    private static final AliexpressAdEntityRelationalModel aliexpressAdEntityRelationalModel = new AliexpressAdEntityRelationalModel();
    private static final AliexpressAdLinkEntityRelationalModel aliexpressAdLinkEntityRelationalModel = new AliexpressAdLinkEntityRelationalModel();
    private static final CrawlerHelper crawlerHelper = new CrawlerHelper();
    private static Double parseMonetaryStrings(String monetaryString) {
        double result;
        Matcher matcher = numericPattern.matcher(monetaryString);
        StringBuilder finalString = new StringBuilder();

        while (matcher.find()) {
            String onlyNumbersString = matcher.group(0);
            finalString.append(onlyNumbersString);
        }

        String readyToParseString = finalString.substring(0, finalString.length()-2)
        + "." + finalString.substring(finalString.length()-2);
        result = Double.parseDouble(readyToParseString);

        return result;
    }

    private static Integer parseQuantityString(String quantityString) {
        Integer quantity = null;
        Matcher matcher = numericPattern.matcher(quantityString);

        if (matcher.find()) {
            quantity = Integer.parseInt(matcher.group(0));
        }
        return quantity;
    }

    private static Long parseEstimatedDeliveryDate(String estimateDeliveryDate) {
        estimateDeliveryDate = estimateDeliveryDate
                .replace("Fev", "Feb")
                .replace("Abr", "Apr")
                .replace("Mai", "May")
                .replace("Ago", "Aug")
                .replace("Set", "Sep")
                .replace("Out", "Oct")
                .replace("Dez", "Dec")
        ;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy");
        Matcher matcher = estimatedDatePattern.matcher(estimateDeliveryDate);
        if (matcher.find()) {
            String dateStringThisYear = matcher.group(0) + ", " + LocalDate.now().getYear();
            String dateStringNextYear = matcher.group(0) + ", " + (LocalDate.now().getYear()+1);

            LocalDate dateOfThisYear = LocalDate.parse(dateStringThisYear, formatter);
            LocalDate dateOfNextYear = LocalDate.parse(dateStringNextYear, formatter);
            LocalDate todayDate = LocalDate.now();

            long differenceToDateThisYear = ChronoUnit.DAYS.between(todayDate, dateOfThisYear);
            long differenceToDateNextYear = ChronoUnit.DAYS.between(todayDate, dateOfNextYear);

            if (differenceToDateThisYear > 0 && differenceToDateThisYear < differenceToDateNextYear) {
                return differenceToDateThisYear;
            } else {
                return differenceToDateNextYear;
            }
        }

        return null;
    }

    public static Integer parsePercent(String percentString) {
        Integer result = null;
        Matcher matcher = percentPattern.matcher(percentString);
        if (matcher.find()) {
            result = Integer.parseInt(matcher.group(0));
        }
        return result;
    }

    private static Boolean verifyIfTheProductIsChoice(WebDriver driver) {
        try {
            driver.findElement(By.cssSelector(choiceBannerCssSelector));
            return true;
        } catch (NoSuchElementException e) {
            logger.severe("Couldn't fetch choice info");
            return false;
        }
    }

    private static String tryToGetWebElement(WebDriver driver, String identifier, boolean isCssSelector) {
        WebElement element;
        if (isCssSelector) {
            element = driver.findElement(By.cssSelector(identifier));
        } else {
            element = driver.findElement(By.xpath(identifier));
        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
        return element.getText();
    }

    private static Map<String, Object> buildMapForProperties(String[] baseStringToBuildMap) {
        System.out.println(Arrays.toString(baseStringToBuildMap));
        Map<String, Object> tempMapForProperties = new HashMap<>();
        for (int arrayIndex = 0; arrayIndex <= baseStringToBuildMap.length-1; arrayIndex++) {
            tempMapForProperties.put(
                    baseStringToBuildMap[arrayIndex].strip(),
                    baseStringToBuildMap[arrayIndex+1]
                            .replace(baseStringToBuildMap[arrayIndex], "")
                            .replaceAll("[()]", "")
                            .strip()
                            .split("\n")
            );

            System.out.println(
                    Arrays.toString(baseStringToBuildMap[arrayIndex + 1]
                            .replace(baseStringToBuildMap[arrayIndex], "")
                            .replaceAll("[()]", "")
                            .strip()
                            .split("\n"))
            );
        }
        return tempMapForProperties;
    }

    public static void main(String[] args) {
        aliexpressAdEntityRelationalModel.createAliAdsTable();

        try (ResultSet allAdsResultSet = aliexpressAdLinkEntityRelationalModel.selectAllAdLinks()) {
//            WebDriver driver = new FirefoxDriver(crawlerHelper.firefoxOptions);
            WebDriver driver = new FirefoxDriver();
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

            while (allAdsResultSet.next()) {
                AliexpressAdModel adModel = new AliexpressAdModel();
                try {
                    adModel.skuId = allAdsResultSet.getLong(1);
                    adModel.link = allAdsResultSet.getString(2);
                    adModel.collectTimestamp = Timestamp.from(Instant.now());

                    logger.info("Querying product: " + adModel.skuId);

                    driver.get(adModel.link);
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(titleCssSelector)));

                    adModel.title = tryToGetWebElement(driver, titleCssSelector, true);
                    System.out.println("title:" + adModel.title);

                    adModel.oldPrice = parseMonetaryStrings(tryToGetWebElement(driver, oldPriceCssSelector, true));
                    System.out.println("oldPrice:" + adModel.oldPrice);

                    adModel.price = parseMonetaryStrings(tryToGetWebElement(driver, priceCssSelector, true));
                    System.out.println("price:" + adModel.price);

                    adModel.discountPercent = parsePercent(tryToGetWebElement(driver, discountPercentCssSelector, true));
                    System.out.println("discountPercent:" + adModel.discountPercent);

                    try {
                        adModel.estimatedTaxValue = parseMonetaryStrings(
                                tryToGetWebElement(driver, estimatedTaxCssSelector, true));
                        System.out.println("estimatedTaxValue: " + adModel.estimatedTaxValue);
                    } catch (NoSuchElementException e) {
                        logger.severe("Couldn't fetch estimated tax info");
                    }

                    try {
                        adModel.quantitySold = parseQuantityString(
                                tryToGetWebElement(driver, quantitySoldCssSelector, true));
                        System.out.println("quantitySold: " + adModel.quantitySold);
                    } catch (NoSuchElementException e) {
                        logger.severe("Couldn't fetch quantity sold info");
                    }

                    try {
                        String[] arrayOfProductsVariations = tryToGetWebElement(driver, productsVariationsChoiceCssSelector, true).split(":");
                        adModel.productsVariations = buildMapForProperties(arrayOfProductsVariations);
                        System.out.println("productsVariations" + adModel.productsVariations);
                    } catch (NoSuchElementException e) {
                        logger.severe("Couldn't fetch products variation info");
                    }

                    try {
                        String shippingInfoString = tryToGetWebElement(driver, shippingCssSelector, true);
                        if (shippingInfoString.matches("[0-9]+")) {
                            adModel.shippingCost = parseMonetaryStrings(tryToGetWebElement(driver, shippingCssSelector, true));
                        } else {
                            adModel.shippingCost = 0.0;
                        }
                        System.out.println("shippingCost: " + adModel.shippingCost);
                    } catch (NoSuchElementException e) {
                        logger.severe("Couldn't fetch shipping cost info");
                    }

                    adModel.isChoice = verifyIfTheProductIsChoice(driver);

                    try {
                        if (driver.findElement(By.cssSelector(valueInInstallmentsOrNationalProductCssSelector))
                                .getText().contains("x")) {
                            adModel.installmentsNumber = Integer.parseInt(
                                    tryToGetWebElement(driver, valueInInstallmentsOrNationalProductCssSelector, true)
                                            .split("x")[0]);

                            adModel.installmentsValue = parseMonetaryStrings(
                                    tryToGetWebElement(driver, valueInInstallmentsOrNationalProductCssSelector, true)
                                            .split("x")[1]);

                            System.out.println("installmentsValue: " + adModel.installmentsValue);
                            System.out.println("installmentsNumber: " + adModel.installmentsNumber);

                            adModel.nationalProduct = false;
                        } else {
                            adModel.nationalProduct = true;
                        }
                    } catch (NoSuchElementException e) {
                        logger.severe("Couldn't fetch instalments info");
                    }

                    try {
                        adModel.estimatedDeliveryInDays = parseEstimatedDeliveryDate(
                                tryToGetWebElement(driver, estimatedDeliveryDateCssSelectorForNonChoiceProducts, true));
                    } catch (NoSuchElementException e) {
                        logger.severe("Couldn't fetch estimated delivery date info. Trying to get with choice css selector");

                        try {
                            adModel.estimatedDeliveryInDays = parseEstimatedDeliveryDate(
                                    tryToGetWebElement(driver, estimatedDeliveryDateCssSelectorForChoiceProducts, true));
                        } catch (NoSuchElementException ex) {
                            logger.severe("Couldn't fetch any estimated delivery info");
                        }
                    }
                    System.out.println("estimatedDeliveryInDays: " + adModel.estimatedDeliveryInDays);

                    // Above part
                    ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500)");
                    // Below part

                    WebElement preSpecificationsDivWebElement = driver.findElement(By.cssSelector(preSpecificationsDivCssSelector));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", preSpecificationsDivWebElement);

                    WebElement specificationsDivWebElement = driver.findElement(By.cssSelector(specificationsCssSelector));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", specificationsDivWebElement);

                    try {
                        String[] arrayOfSpecifications = tryToGetWebElement(driver, specificationsCssSelector, true).split("\n");
                        adModel.specifications = buildMapForProperties(arrayOfSpecifications);
                        System.out.println("specifications: " + adModel.specifications);
                    } catch (NoSuchElementException e) {
                        logger.severe("Couldn't fetch specifications info");
                    }

                    try {
                        Matcher totalReviewsMatcher = numericPattern.matcher(
                                tryToGetWebElement(driver, totalReviewsMainCssSelector, true));
                        if (totalReviewsMatcher.find()) {
                            adModel.totalReviews = Integer.parseInt(totalReviewsMatcher.group(0));
                        }
                    } catch (NoSuchElementException e) {
                        logger.severe("Couldn't fetch total reviews info");
                    }
                    System.out.println("totalReviews: " + adModel.totalReviews);

                    try {
                        adModel.averageReview = Double.parseDouble(tryToGetWebElement(driver, averageReviewCssSelector, true));
                        System.out.println("averageReview: " + adModel.averageReview);
                    } catch (NoSuchElementException e) {
                        logger.severe("Couldn't fetch average review info");
                    }

                    try {
                        String[] arrayOfReviewIndicators = tryToGetWebElement(driver, reviewIndicatorsCssSelector, true).split("\n");
                        adModel.reviewIndicators = buildMapForProperties(arrayOfReviewIndicators);
                        System.out.println("reviewIndicators: " + adModel.reviewIndicators);
                    } catch (NoSuchElementException e) {
                        logger.severe("Couldn't fetch review indicators info");
                    }

                    List<AliexpressAdModel> specificAdResult = aliexpressAdEntityRelationalModel.selectSpecificAd(adModel.skuId);

                    if (specificAdResult.isEmpty()) {
                        aliexpressAdEntityRelationalModel.insertNewAd(adModel);
                    } else {
                        aliexpressAdLinkEntityRelationalModel.deleteSpecificAd(adModel.skuId);
                    }
                } catch (WebDriverException webDriverException) {
                    aliexpressAdLinkEntityRelationalModel.deleteSpecificAd(adModel.skuId);
                    logger.severe(webDriverException.toString());
                }
            }
            // Driver kill
            driver.quit();
        } catch (SQLException e) {
            logger.severe(e.toString());
        }
        logger.fine("Finished ads crawling");
    }
}
