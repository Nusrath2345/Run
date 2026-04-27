package com.run.dto;

// request body for the link checker endpoint
// jackson needs the no-arg constructor + setter to deserialize this
public class LinkScanRequest {

    private String url;

    public LinkScanRequest() {}

    public LinkScanRequest(String url) {
        this.url = url;
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}