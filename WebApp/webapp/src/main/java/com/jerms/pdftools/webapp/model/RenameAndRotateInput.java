package com.jerms.pdftools.webapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RenameAndRotateInput {
    @JsonProperty("source")
    public String source;
    @JsonProperty("destination")
    public String destination;
    @JsonProperty("rotate")
    public boolean rotate;
    @JsonProperty("rename")
    public boolean rename;
    @JsonProperty("rename-source")
    public String renameSource;
    @JsonProperty("rename-list")
    public String renameList;
}
