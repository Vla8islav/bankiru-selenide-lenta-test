package com.softwaretestingtraning.app;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.codeborne.selenide.WebDriverRunner.url;
import static java.lang.Math.pow;


@Test
public class SelenideBankiruLentaPaginationTest {

    @BeforeClass(alwaysRun = true)
    public void testNewsMainPage() {
        System.out.println("Setting Chromium as a default browser.");
        Configuration.browser = "Chrome";
        Configuration.timeout = 10000;
        System.setProperty("webdriver.chrome.driver", "/usr/lib/chromium-browser/chromedriver");
        open("http://www.banki.ru/news/lenta/");
        // Let's disable fullscreen banner
        executeJavaScript("document.addEventListener(\"DOMContentLoaded\", function() {require([\"jquery\"], function ($) {$(\".b-ad-fullscreen\").parent(\".ui-popup-holder\").remove();});});");
    }

    @Test(groups = {"positive", "selenium"})
    public void testUrlShouldContainPageNumber() {
        openNextPage();
        String currentPageAccordingToPagination = getCurrentPagePaginationNumber();
        Assert.assertTrue(url().contains(currentPageAccordingToPagination),
                "The current url " + url() + "does not contain current active pagination number, which is " + currentPageAccordingToPagination);
    }

    @Test(groups = {"positive", "selenium"})
    public void testLastPageShouldNotContainNextPageArrow()
    {
        openLastPage();
        String nextArrowSelector = ".ui-pagination__item.ui-pagination__next a";
        $(nextArrowSelector).shouldNot(exist);
    }

    @Test(groups = {"positive", "selenium"})
    public void testUrlShouldDecreaseWhenUserMovesBack()
    {
        openLastPage();
        int pageBeforeMovingBack = extractCurrentNewsPageFromTheUrl();
        scrollPaginationIntoView();
        $(".ui-pagination__prev").click();
        int pageAfterMovingBack = extractCurrentNewsPageFromTheUrl();
        Assert.assertTrue(pageAfterMovingBack < pageBeforeMovingBack, "");
    }

    @Test(groups = {"positive", "selenium"})
    public void testFirstPageShouldNotContainPreviousPageArrow()
    {
        openFirstPage();
        scrollPaginationIntoView();
        $(".ui-pagination__prev").shouldNot(exist);
    }

    @Test(groups = {"positive", "selenium"})
    public void testUrlShouldIncreaseWhenUserMovesBack()
    {
        openFirstPage();
        openNextPage();
        int pageBeforeMovingForward = extractCurrentNewsPageFromTheUrl();
        scrollPaginationIntoView();
        $(".ui-pagination__next").click();
        int pageAfterMovingForward = extractCurrentNewsPageFromTheUrl();

        Assert.assertTrue(pageBeforeMovingForward < pageAfterMovingForward, "");
    }

    private int extractCurrentNewsPageFromTheUrl() {

        String pageNumberStr = url().replaceAll("[^\\d]", "");
        return Integer.parseInt(pageNumberStr);
    }

    private void openLastPage() {
        scrollPaginationIntoView();
        if($(".ui-pagination__next").exists()) {
            int numberOfRightArrows = $$(".ui-pagination__next").size();
            String lastPageSelectorCSS = "ul.ui-pagination__list li:nth-last-child(" + Integer.toString(numberOfRightArrows + 1) + ") a";
            $(lastPageSelectorCSS).click();
            scrollPaginationIntoView();
        }
    }

    private void openFirstPage() {
        scrollPaginationIntoView();
        if($(".ui-pagination__prev").exists()) {
            int numberOfLeftArrows = $$(".ui-pagination__prev").size();
            int numberOfPaginationElements = $$(".ui-pagination__item").size();
            String lastPageSelectorCSS = "ul.ui-pagination__list li:nth-last-child(" + Integer.toString(numberOfPaginationElements - numberOfLeftArrows) + ") a";
            $(lastPageSelectorCSS).click();
            scrollPaginationIntoView();
        }
    }

    private String getCurrentPagePaginationNumber() {
        return $(".ui-pagination__list .ui-pagination__item--active a").getText();
    }

    private void openNextPage() {
        scrollPaginationIntoView();
        if($(".ui-pagination__next").isDisplayed()) {
            $(".ui-pagination__list .ui-pagination__next").click();
            scrollPaginationIntoView();
        }
    }

    private void scrollPaginationIntoView() {
        List<String> paginationSelectorsList = new ArrayList<>();
        paginationSelectorsList.add("#news-pagination");
        paginationSelectorsList.add(".layout-column-center div.border-bottom-dotted");
        paginationSelectorsList.add(".layout-columns-wrapper .widget .text-list:last-of-type");
        paginationSelectorsList.add("#comments__pagination");

        for(String currentPaginationSelector : paginationSelectorsList) {
            if($(currentPaginationSelector).exists()) {
                scrollPageToTheElementBySelectorCSS(currentPaginationSelector);
            }
        }
        $(".ui-pagination__list").shouldBe(visible);

    }

    private void scrollPageToTheElementBySelectorCSS(String currentPaginationSelectorCSS) {
        Object height = executeJavaScript("return document.querySelectorAll('" + currentPaginationSelectorCSS + "')[0].offsetHeight + " +
                "document.querySelectorAll('" + currentPaginationSelectorCSS + "')[0].offsetTop;");
        executeJavaScript("window.scrollTo(0," + height.toString() + " + 200);");
    }


    @AfterClass(alwaysRun = true)
    public void exampleAfterMethod() {
        System.out.println("Exiting sample selenide test");
    }
}
