package com.nasko.marauder;

public class PageReference {
    private String url;
    private String host;

    public PageReference(String url) {
        this.url = url;
    }

    public String getHost() {
        if (this.host == null) {
            int lastDotIndex = this.url.lastIndexOf('.');
            int backslashIndex = this.url.indexOf('/', lastDotIndex);

            if (backslashIndex == -1) {
                this.host = this.url;
            } else {
                this.host = this.url.substring(0, backslashIndex);
            }
        }

        return this.host;
    }

    public String getUrl() {
        return url;
    }
}
