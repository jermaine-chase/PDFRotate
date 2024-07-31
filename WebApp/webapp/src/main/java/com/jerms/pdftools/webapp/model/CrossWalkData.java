package com.jerms.pdftools.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CrossWalkData {
    @JsonProperty("fileName")
    public String fileName;
    @JsonProperty("region")
    public String region;
}
