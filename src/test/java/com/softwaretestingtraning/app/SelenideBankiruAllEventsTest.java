package com.softwaretestingtraning.app;

import com.codeborne.selenide.Configuration;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;


@Test
public class SelenideBankiruAllEventsTest {

    private final HelperLentaPagination helperLentaPagination = new HelperLentaPagination();

    @BeforeClass(alwaysRun = true)
    public void testNewsMainPage() {
        System.out.println("Setting Chromium as a default browser.");
        Configuration.browser = "Chrome";
        Configuration.timeout = 10000;
        System.setProperty("webdriver.chrome.driver", "/usr/lib/chromium-browser/chromedriver");
        open("http://www.testbanki.ru/news/");
        // Let's disable fullscreen banner
        executeJavaScript("document.addEventListener(\"DOMContentLoaded\", function() {require([\"jquery\"], function ($) {$(\".b-ad-fullscreen\").parent(\".ui-popup-holder\").remove();});});");
    }

    @Test(groups = {"positive", "selenium"})
    public void testAllEventsPageBreadcrumbsElementsExisting() {
        helperLentaPagination.openNextPage();
        String currentPageAccordingToPagination = helperLentaPagination.getCurrentPagePaginationNumber();
        Assert.assertTrue(url().contains(currentPageAccordingToPagination),
                "The current url " + url() + "does not contain current active pagination number, which is " + currentPageAccordingToPagination);
    }

    @Test(groups = {"positive", "selenium"})
    public void testLastPageShouldNotContainNextPageArrow()
    {
        helperLentaPagination.openLastPage();
        String nextArrowSelector = ".ui-pagination__item.ui-pagination__next a";
        $(nextArrowSelector).shouldNot(exist);
    }

    @Test(groups = {"positive", "selenium"})
    public void testUrlShouldDecreaseWhenUserMovesBack()
    {
        helperLentaPagination.openLastPage();
        int pageBeforeMovingBack = helperLentaPagination.extractCurrentNewsPageFromTheUrl();
        helperLentaPagination.scrollPaginationIntoView();
        $(".ui-pagination__prev").click();
        int pageAfterMovingBack = helperLentaPagination.extractCurrentNewsPageFromTheUrl();
        Assert.assertTrue(pageAfterMovingBack < pageBeforeMovingBack, "");
    }

    @Test(groups = {"positive", "selenium"})
    public void testFirstPageShouldNotContainPreviousPageArrow()
    {
        helperLentaPagination.openFirstPage();
        helperLentaPagination.scrollPaginationIntoView();
        $(".ui-pagination__prev").shouldNot(exist);
    }

    @Test(groups = {"positive", "selenium"})
    public void testUrlShouldIncreaseWhenUserMovesBack()
    {
        helperLentaPagination.openFirstPage();
        helperLentaPagination.openNextPage();
        int pageBeforeMovingForward = helperLentaPagination.extractCurrentNewsPageFromTheUrl();
        helperLentaPagination.scrollPaginationIntoView();
        $(".ui-pagination__next").click();
        int pageAfterMovingForward = helperLentaPagination.extractCurrentNewsPageFromTheUrl();

        Assert.assertTrue(pageBeforeMovingForward < pageAfterMovingForward, "");
    }

    @AfterClass(alwaysRun = true)
    public void exampleAfterMethod() {
        System.out.println("Exiting sample selenide test");
    }
}
