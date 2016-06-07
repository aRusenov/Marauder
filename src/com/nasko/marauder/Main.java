package com.nasko.marauder;

import com.nasko.marauder.helpers.DownloadUtils;

import java.io.File;

public class Main {

    private static final String MY_SITE = "http://clubz.bg/";

    public static void main(String[] args) {

        MarauderClient client = new MarauderClient() {
            @Override
            protected boolean shouldVisit(PageReference page) {
                return page.getHost().equals(MY_SITE);
            }

            @Override
            protected void onPageVisit(PageData page) {
                Iterable<String> pageImageUrls = page.getPageImages();
                for (String imageUrl : pageImageUrls) {
                    int lastBackslashIndex = imageUrl.lastIndexOf('/');
                    String imageName = imageUrl.substring(lastBackslashIndex);

                    DownloadUtils.downloadImage(imageUrl, "images", imageName);
                }
            }
        };

        client.setWorkerCount(4);
        client.seed(MY_SITE);

        client.run();
    }
}
