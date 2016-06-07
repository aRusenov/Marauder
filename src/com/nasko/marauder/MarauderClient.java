package com.nasko.marauder;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MarauderClient {

    private static final int DEFAULT_WORKERS_COUNT = 4;

    private Thread[] workerThreads;
    private int workerCount;

    private long timeout;
    private AtomicBoolean running;
    private BlockingQueue<PageReference> pendingPages;

    private long pageLimit;
    private long visitedPagesCount;

    public MarauderClient(int workers) {
        this.setWorkerCount(workers);
        this.running = new AtomicBoolean();
        this.pendingPages = new BlockingQueue<>();
    }

    public MarauderClient() {
        this(DEFAULT_WORKERS_COUNT);
    }

    public void setWorkerCount(int workerCount) {
        if (workerCount <= 0) {
            throw new IllegalArgumentException("Worker count must be a positive integer number");
        }

        this.workerCount = workerCount;
    }

    public void run() {
        if (this.running.get()) {
            throw new UnsupportedOperationException("The client is already running");
        }

        this.running.set(true);

        this.workerThreads = new Thread[this.workerCount];
        for (int i = 0; i < this.workerThreads.length; i++) {
            this.workerThreads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    runWorker();
                }
            });

            this.workerThreads[i].start();
        }

        for (int i = 0; i < this.workerThreads.length; i++) {
            try {
                this.workerThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        this.running.set(false);
    }

    protected abstract boolean shouldVisit(PageReference page);

    protected abstract void onPageVisit(PageData page);

    private void runWorker() {

        // TODO: add page limit
        while (this.running.get()) {
            PageReference currentPage = this.pendingPages.remove();
            CloseableHttpClient client = HttpClients.createDefault();

            CloseableHttpResponse response = null;
            try {
                response = client.execute(new HttpGet(currentPage.getUrl()));
                PageData page = new PageData(response, currentPage.getUrl(), currentPage.getHost());

                this.onPageVisit(page);
                for (String url : page.getOutgoingPages()) {
                    PageReference pageReference = new PageReference(url);
                    if (this.shouldVisit(pageReference)) {
                        this.pendingPages.add(pageReference);
                    }
                }
            } catch (IOException e) {
                // TODO: log error
                continue;
            } finally {
                if (response != null) {
                    try {
                        response.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setTimeout(long millis) {
        if (this.running.get()) {
            throw new UnsupportedOperationException("setTimeout() must be called before run()");
        }

        if (millis <= 0) {
            throw new IllegalArgumentException("Timeout must be a positive integer number");
        }

        this.timeout = millis;
    }

    public void seed(String url) {
//        if (! UrlUtils.isValid(url)) {
//            throw new IllegalArgumentException(url  + " is not a valid URL");
//        }

        this.pendingPages.add(new PageReference(url));
    }

    public void runAsync(OnCompleteListener listener) {
    }
}
