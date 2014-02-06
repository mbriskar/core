package org.jboss.weld.environment.se.discovery.url;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import org.jboss.jandex.CompositeIndex;
import org.jboss.jandex.IndexView;


public class IndexingThreadFactory implements ThreadFactory {

    List<IndexingThread> allStartedThreads = new ArrayList<IndexingThread>();

    @Override
    public Thread newThread(Runnable runnable) {
        IndexingThread thread = new IndexingThread(runnable);
        allStartedThreads.add(thread);
        return thread;
    }

    public IndexView buildIndex() {
        stopThreads();
        List<IndexView> indexes = new ArrayList<IndexView>();
       for (IndexingThread thread : allStartedThreads) {
            IndexView index = thread.getIndexer().complete();
           indexes.add(index);
       }
        CompositeIndex composite = CompositeIndex.create(indexes);
        return composite;
    }

    private void stopThreads() {
        for (Thread thread : allStartedThreads) {
            thread.interrupt();
        }
    }

}
