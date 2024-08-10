package com.jerms.pdftools.webapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MarketingDataTest {

    private MarketingData marketingData;

    @BeforeEach
    public void setUp() {
        marketingData = new MarketingData();
        marketingData.title = "Test Title";
        marketingData.pstageUrl = "http://pstage-url.com";
        marketingData.prodUrl = "http://prod-url.com";
        marketingData.region = "ps";
        marketingData.percent = 0.2;
        marketingData.exportFile = "test-export-file.xlsx";
    }

    @Test
    public void testGetUrlForPStageRegion() {
        marketingData.region = "ps";
        assertEquals("http://pstage-url.com", marketingData.getUrl());
    }

    @Test
    public void testGetUrlForOtherRegion() {
        marketingData.region = "prod";
        assertEquals("http://prod-url.com", marketingData.getUrl());
    }

    @Test
    public void testGetHTMLResponseAddedToExport() {
        String expectedResponse = "<div class='added'>" +
                "<span class='plan-link my-warning'>" +
                "<a href='http://pstage-url.com' target='_blank'>Plan Link</a></span>" +
                "<span class='plan-name my-warning'> Test Title</span><span class='my-warning'>80.0</span></div>";
        assertEquals(expectedResponse, marketingData.getHTMLResponse(true));
    }

    @Test
    public void testGetHTMLResponseNotAddedToExport() {
        String expectedResponse = "<div class='not-added'>" +
                "<span class='plan-link my-warning'>" +
                "<a href='http://pstage-url.com' target='_blank'>Plan Link</a></span>" +
                "<span class='plan-name my-warning'> Test Title</span><span class='my-warning'>80.0</span></div>";
        assertEquals(expectedResponse, marketingData.getHTMLResponse(false));
    }

    @Test
    public void testGetHTMLResponsePdfNotFound() {
        marketingData.percent = -1.0;
        String expectedResponse = "<div class='not-added'>" +
                "<span class='plan-link my-alert'><a href='http://pstage-url.com' target='_blank'>Plan Link</a></span><span class='plan-name my-alert'> Test Title</span><span class='my-alert'>PDF NOT FOUND!</span></div>";
        assertEquals(expectedResponse, marketingData.getHTMLResponse(false));
    }
}
