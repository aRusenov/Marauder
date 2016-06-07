package com.nasko.marauder.iterators;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageUrlRegexIterator implements Iterator<String> {

    private static final String IMG_REGEX = "<img.*?src=\"(.*?)\".*?>";
    private static final Pattern PATTERN = Pattern.compile(IMG_REGEX);

    private String html;
    private String host;
    private Matcher matcher;

    public ImageUrlRegexIterator(String html, String host) {
        this.html = html;
        this.matcher = PATTERN.matcher(html);
        this.host = host;
    }

    public String getHtml() {
        return this.html;
    }

    public String getHost() {
        return this.host;
    }

    @Override
    public boolean hasNext() {
        return this.matcher.find();
    }

    @Override
    public String next() {
        String url = this.matcher.group(1);
        return this.resolveUrl(url);
    }

    private String resolveUrl(String pageUrl) {
        if (pageUrl.charAt(0) == '/') {
            // Url is relative
            return this.host + pageUrl;
        }

        return pageUrl;
    }
}
