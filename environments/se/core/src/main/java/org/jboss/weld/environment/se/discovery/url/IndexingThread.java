package org.jboss.weld.environment.se.discovery.url;

import org.jboss.jandex.Indexer;

public class IndexingThread extends Thread {
    private Indexer indexer = new Indexer();

    public IndexingThread() {
        super();
    }

    public IndexingThread(Runnable runnable) {
        super(runnable);
    }

    public Indexer getIndexer() {
        return indexer;
    }
}
