package com.nasko.marauder.iterators;

import java.util.Iterator;

public class PageImageUrlCollection implements Iterable<String> {

    private String html;
    private String host;

    public PageImageUrlCollection(String html, String host) {
        this.html = html;
        this.host = host;
    }

    @Override
    public Iterator<String> iterator() {
        return new ImageUrlRegexIterator(this.html, this.host);
    }
}
