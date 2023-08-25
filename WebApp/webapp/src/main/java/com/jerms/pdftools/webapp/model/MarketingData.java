package com.jerms.pdftools.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MarketingData {
    @JsonProperty("Title")
    public String title;
    @JsonProperty("P-Stage URL")
    public String pstageUrl;
    @JsonProperty("Production URL")
    public String prodUrl;

    public String region;

    public Double percent;

    public String exportFile;

    public String getUrl() {
        if ("ps".equalsIgnoreCase(region)) {
            return pstageUrl;
        }
        return prodUrl;
    }

    public String getHTMLResponse(boolean addedToExport) {
        if (percent >= 0.0) {
            return "<div class='" + (addedToExport?"":"not-") + "added'>" +
                    "<span class='plan-link" + (percent > 0.1?" my-warning": "") + "'>" +
                    "<a href='" + getUrl() + "' target='_blank'>Plan Link</a></span>" +
                    "<span class='plan-name" + (percent > 0.1?" my-warning": "") + "'> "
                    + title + "</span><span"+ (percent > 0.1?"  class='my-warning'": "") + ">" + (100 - percent * 100) + "</span></div>";
        }
        return "<div class='" + (addedToExport?"":"not-") + "added'>" +
                "<span class='plan-link my-alert'><a href='" + getUrl() + "' target='_blank'>Plan Link</a></span><span class='plan-name my-alert'> "
                + title + "</span><span class='my-alert'>PDF NOT FOUND!</span></div>";
    }
}
