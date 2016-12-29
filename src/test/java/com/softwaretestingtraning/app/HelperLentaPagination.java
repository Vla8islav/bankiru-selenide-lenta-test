package com.softwaretestingtraning.app;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class HelperLentaPagination {
    HelperLentaPagination() {
    }

    int extractCurrentNewsPageFromTheUrl() {

        String currentUrl = WebDriverRunner.url();
        int currentPageNumber = 0;
        try {
            URL aURL = new URL(currentUrl);
            String pageNumberStr = aURL.getPath().replaceAll("[^\\d]", "");
            currentPageNumber += Integer.parseInt(pageNumberStr);
        } catch (MalformedURLException e) {
            System.err.print("Malformed current url " + currentUrl);
        }
        return currentPageNumber;
    }

    void openLastPage() {
        scrollPaginationIntoView();
        if (Selenide.$(".ui-pagination__next").exists()) {
            int numberOfRightArrows = Selenide.$$(".ui-pagination__next").size();
            String lastPageSelectorCSS = "ul.ui-pagination__list li:nth-last-child(" + Integer.toString(numberOfRightArrows + 1) + ") a";
            Selenide.$(lastPageSelectorCSS).click();
            scrollPaginationIntoView();
        }
    }

    void openFirstPage() {
        scrollPaginationIntoView();
        if (Selenide.$(".ui-pagination__prev").exists()) {
            int numberOfLeftArrows = Selenide.$$(".ui-pagination__prev").size();
            int numberOfPaginationElements = Selenide.$$(".ui-pagination__item").size();
            String lastPageSelectorCSS = "ul.ui-pagination__list li:nth-last-child(" + Integer.toString(numberOfPaginationElements - numberOfLeftArrows) + ") a";
            Selenide.$(lastPageSelectorCSS).click();
            scrollPaginationIntoView();
        }
    }

    String getCurrentPagePaginationNumber() {
        return Selenide.$(".ui-pagination__list .ui-pagination__item--active a").getText();
    }

    void openNextPage() {
        scrollPaginationIntoView();
        if (Selenide.$(".ui-pagination__next").isDisplayed()) {
            Selenide.$(".ui-pagination__list .ui-pagination__next").click();
            scrollPaginationIntoView();
        }
    }

    void scrollPaginationIntoView() {
        List<String> paginationSelectorsList = new ArrayList<>();
        paginationSelectorsList.add("#news-pagination");
        paginationSelectorsList.add(".layout-column-center div.border-bottom-dotted");
        paginationSelectorsList.add(".layout-columns-wrapper .widget .text-list:last-of-type");
        paginationSelectorsList.add("#comments__pagination");

        for (String currentPaginationSelector : paginationSelectorsList) {
            if (Selenide.$(currentPaginationSelector).exists()) {
                scrollPageToTheElementBySelectorCSS(currentPaginationSelector);
            }
        }
        Selenide.$(".ui-pagination__list").shouldBe(Condition.visible);

    }

    private void scrollPageToTheElementBySelectorCSS(String currentPaginationSelectorCSS) {
        Object height = Selenide.executeJavaScript("return document.querySelectorAll('" + currentPaginationSelectorCSS + "')[0].offsetHeight + " +
                "document.querySelectorAll('" + currentPaginationSelectorCSS + "')[0].offsetTop;");
        Selenide.executeJavaScript("window.scrollTo(0," + height.toString() + " + 200);");
    }
}