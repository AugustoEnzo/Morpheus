package com.fuse.crawlers;

import com.fuse.sql.erm.AliexpressAdEntityRelationalModel;
import com.fuse.sql.erm.AliexpressAdLinksEntityRelationalModel;
import com.fuse.sql.models.AliexpressAdModel;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
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
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AliexpressAds implements com.fuse.sql.constants.AliexpressAds, Runnable {
    static Logger logger = Logger.getLogger(AliexpressAds.class.getName());
    static Pattern numericPattern = Pattern.compile("[0-9]", Pattern.CASE_INSENSITIVE);
    static Pattern estimatedDatePattern = Pattern.compile("[0-9]{2} [a-zA-Z]{3}", Pattern.CASE_INSENSITIVE);
    static Pattern percentPattern = Pattern.compile("[0-9]{1,3}", Pattern.CASE_INSENSITIVE);
    static AliexpressAdEntityRelationalModel aliexpressAdEntityRelationalModel = new AliexpressAdEntityRelationalModel();
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
    public void run() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.addArguments("--headless", "--disable-gpu", "--blink-settings=imagesEnabled=false");

        WebDriver driver = new FirefoxDriver(firefoxOptions);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        AliexpressAdLinksEntityRelationalModel aliexpressAdLinksEntityRelationalModel = new AliexpressAdLinksEntityRelationalModel();

        try (ResultSet allAdsResultSet = aliexpressAdLinksEntityRelationalModel.selectAllAdLinksInNormalOrder()) {
            try {
                while (allAdsResultSet.next()) {
                    AliexpressAdModel adModel = new AliexpressAdModel();
                    try {
                        adModel.skuId = allAdsResultSet.getLong(1);
                        adModel.link = allAdsResultSet.getString(2);
                        adModel.collectTimestamp = Timestamp.from(Instant.now());

                        logger.info("Querying product: " + adModel.skuId);

                        driver.get(adModel.link);
                        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(titleCssSelector)));

                        adModel.title = driver.findElement(By.cssSelector(titleCssSelector)).getText();

                        adModel.oldPrice = parseMonetaryStrings(driver.findElement(By.cssSelector(oldPriceCssSelector)).getText());

                        adModel.price = parseMonetaryStrings(driver.findElement(By.cssSelector(priceCssSelector)).getText());

                        adModel.discountPercent = parsePercent(driver.findElement(
                                By.cssSelector(discountPercentCssSelector)).getText());

                        try {
                            if (driver.findElement(By.cssSelector(valueInInstallmentsOrNationalProductCssSelector))
                                    .getText().contains("x")) {
                                adModel.installmentsNumber = Integer.parseInt(driver.findElement(By.cssSelector(valueInInstallmentsOrNationalProductCssSelector))
                                        .getText().split("x")[0]);

                                adModel.installmentsValue = parseMonetaryStrings(driver.findElement(By.cssSelector(valueInInstallmentsOrNationalProductCssSelector))
                                        .getText().split("x")[1]);

                                adModel.nationalProduct = false;
                            } else {
                                adModel.nationalProduct = true;
                            }
                        } catch (NoSuchElementException e) {
                            logger.severe("Couldn't fetch instalments info");
                        }

                        try {
                            adModel.estimatedTaxValue = parseMonetaryStrings(driver.findElement(By.cssSelector(estimatedTaxCssSelector))
                                    .getText());
                        } catch (NoSuchElementException e) {
                            logger.severe("Couldn't fetch estimated tax info");
                        }

                        try {
                            adModel.quantitySold = parseQuantityString(driver.findElement(By.cssSelector(quantitySoldCssSelector))
                                    .getText());
                        } catch (NoSuchElementException e) {
                            logger.severe("Couldn't fetch quantity sold info");
                        }

                        try {
                            String shippingInfoString = driver.findElement(By.cssSelector(shippingCssSelector)).getText();
                            if (shippingInfoString.matches("[0-9]")) {
                                adModel.shippingCost = parseMonetaryStrings(driver.findElement(By.cssSelector(shippingCssSelector)).getText());
                            }
                        } catch (NoSuchElementException e) {
                            logger.severe("Couldn't fetch shipping cost info");
                        }

                        try {
                            adModel.estimatedDeliveryInDays = parseEstimatedDeliveryDate(
                                    driver.findElement(By.cssSelector(estimatedDeliveryDateCssSelectorForNonChoiceProducts)).getText());
                        } catch (NoSuchElementException e) {
                            logger.severe("Couldn't fetch estimated delivery date info. Trying to get with choice css selector");

                            try {
                                adModel.estimatedDeliveryInDays = parseEstimatedDeliveryDate(
                                        driver.findElement(By.cssSelector(estimatedDeliveryDateCssSelectorForChoiceProducts)).getText());
                            } catch (NoSuchElementException ex) {
                                logger.severe("Couldn't fetch any estimated delivery info");
                            }
                        }

    //                    WebElement productsVariations = driver.findElement(By.cssSelector(productVariationsCssSelector));

                        adModel.isChoice = verifyIfTheProductIsChoice(driver);

                        List<AliexpressAdModel> specificAdResult = aliexpressAdEntityRelationalModel.selectSpecificAd(adModel.skuId);

                        if (specificAdResult.isEmpty()) {
                            aliexpressAdEntityRelationalModel.insertNewAd(adModel);
                        } else {
                            aliexpressAdLinksEntityRelationalModel.deleteSpecificAd(adModel.skuId);
                        }

                    } catch (WebDriverException webDriverException) {
                        aliexpressAdLinksEntityRelationalModel.deleteSpecificAd(adModel.skuId);
                        logger.severe(webDriverException.toString());
                    }
                }
            } catch (SQLException sqlException) {
                logger.severe(sqlException.toString());
            }
        } catch (SQLException e) {
            logger.severe(e.toString());
        }
        logger.fine("Finished ads crawling");
    }
}
