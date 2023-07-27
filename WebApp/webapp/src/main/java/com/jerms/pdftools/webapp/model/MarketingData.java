package com.jerms.pdftools.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jerms.pdftools.webapp.util.pdf.PdfExplorer;

public class MarketingData {
    @JsonProperty("Title")
    public String title;
    @JsonProperty("P-Stage URL")
    public String pstageUrl;
    @JsonProperty("Production URL")
    public String prodUrl;

    public String getHTMLResponse(Double percent) {
        return "<span class='plan-link'><a href='" + pstageUrl + "' target='_blank'>Plan Link</a></span><span class='plan-name'> "
                + title + "</span><span>" + (100 - percent * 100) + "</span>";
    }
}
