package com.nasko.marauder;

import com.nasko.marauder.iterators.ImageUrlRegexIterator;
import com.nasko.marauder.iterators.PageImageUrlCollection;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageData {

    private static final String URL_REGEX = "<a href=\\\"(.*?)\\\".*?>";
    private static final Pattern PATTERN = Pattern.compile(URL_REGEX);

    private String url;
    private String host;
    private CloseableHttpResponse response;
    private List<String> outgoingPages;
    private String html;

    public PageData(CloseableHttpResponse response, String url, String host) {
        this.response = response;
        this.url = url;
        this.host = host;
    }

    public String getUrl() {
        return this.url;
    }

    public String getHost() {
        return this.host;
    }

    public Iterable<String> getPageImages() {
        return new PageImageUrlCollection(this.getHtml(), this.host);
    }

    public List<String> getOutgoingPages() {
        if (this.outgoingPages == null) {
            this.outgoingPages = new ArrayList<>();
            String html = this.getHtml();
            Matcher matcher = PATTERN.matcher(html);
            while (matcher.find()) {
                String pageUrl = html.substring(matcher.start(1), matcher.end(1));
                if (pageUrl.equals("#")) {
                    continue;
                }

                pageUrl = this.resolveUrl(pageUrl);
                this.outgoingPages.add(pageUrl);
            }
        }

        return this.outgoingPages;
    }

    private String resolveUrl(String pageUrl) {
        if (pageUrl.charAt(0) == '/') {
            // Url is relative
            return this.host + pageUrl;
        }

        return pageUrl;
    }

    public String getHtml() {
        if (this.html == null) {
            try {
                this.html = getBody();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return this.html;
    }

    private String getBody() throws IOException {
        InputStream input = this.response.getEntity().getContent();
        int contentLength = (int) this.response.getEntity().getContentLength();
        byte[] bytes = new byte[contentLength];
        IOUtils.read(input, bytes);

        String result = new String(bytes, StandardCharsets.UTF_8);
        return result;
    }
}
