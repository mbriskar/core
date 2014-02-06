/**
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.environment.se.discovery.url;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.jboss.logging.Logger;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.IndexView;

/**
 * This class provides file-system orientated scanning
 *
 * @author Pete Muir
 * @author Marko Luksa
 */
public class FileSystemURLHandler {

    private static final int timeToWaitForThreads = 50;
    private static final Logger log = Logger.getLogger(FileSystemURLHandler.class);
    private static final String UNEXPECTED_CLASSLOADER_MESSAGE = "could not invoke JNLPClassLoader#getJarFile(URL) on context class loader, expecting Web Start class loader";

    private static final String CLASS_FILE_EXTENSION = ".class";
    private static final String BEANS_XML = "beans.xml";

    private List<String> discoveredClasses = new ArrayList<String>();
    private List<URL> discoveredBeansXmlUrls = new ArrayList<URL>();
    private Indexer indexer = new Indexer();
    IndexingThreadFactory threadFactory;
    ExecutorService executor;

    public FileSystemURLHandler() {
        threadFactory = new IndexingThreadFactory();
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1, threadFactory);
    }

    public void handle(String urlPath) {

        try {
            log.tracev("scanning: {0}", urlPath);
            // WebStart support: get path to local cached copy of remote JAR file
            if (urlPath.startsWith("http:") || urlPath.startsWith("https:")) {
                // Class loader should be an instance of com.sun.jnlp.JNLPClassLoader
                ClassLoader jnlpClassLoader = WeldSEResourceLoader.getClassLoader();
                try {
                    // Try to call com.sun.jnlp.JNLPClassLoader#getJarFile(URL) from JDK 6
                    Method m = jnlpClassLoader.getClass().getMethod("getJarFile", URL.class);
                    // returns a reference to the local cached copy of the JAR
                    ZipFile jarFile = (ZipFile) m.invoke(jnlpClassLoader, new URL(urlPath));
                    urlPath = jarFile.getName();
                } catch (MalformedURLException mue) {
                    log.warn("could not read entries, method JNLPClassLoader#getJarFile(URL) did not return a valid URL", mue);
                } catch (NoSuchMethodException nsme) {
                    log.warn(UNEXPECTED_CLASSLOADER_MESSAGE, nsme);
                } catch (IllegalArgumentException iarge) {
                    log.warn(UNEXPECTED_CLASSLOADER_MESSAGE, iarge);
                } catch (InvocationTargetException ite) {
                    log.warn("JNLPClassLoader#getJarFile(URL) threw exception", ite);
                } catch (Exception iacce) {
                    log.warn("could not invoke JNLPClassLoader#getJarFile(URL) on context class loader", iacce);
                }
            }
            File file = new File(urlPath);
            if (file.isDirectory()) {
                handleDirectory(file, null);
            } else {
                handleArchiveByFile(file);
            }
        } catch (IOException ioe) {
            log.warn("could not read entries", ioe);
        }
    }

    private void handleArchiveByFile(File file) throws IOException {
        try {
            log.tracev("archive: {0}", file);
            String archiveUrl = "jar:" + file.toURI().toURL().toExternalForm() + "!/";
            ZipFile zip = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                addToDiscovered(name, new URL(archiveUrl + name));
            }
            zip.close();
        } catch (ZipException e) {
            throw new RuntimeException("Error handling file " + file, e);
        }
    }

    private void addToIndex(final URL url) {
            executor.execute(new Runnable() {
                public void run() {
                InputStream fs = null;
                    try {
                        fs = url.openStream();
                        IndexingThread indexThread = (IndexingThread) Thread.currentThread();
                        Indexer indexer = indexThread.getIndexer();
                        indexer.index(fs);
                    } catch (IOException ex) {
                } finally {
                    try {
                        if (fs != null) {
                            fs.close();
                        }
                    } catch (IOException ex) {

                    }

                    }
                }
            });
    }

    public IndexView buildIndex() {
        executor.shutdown();
        try {
            executor.awaitTermination(timeToWaitForThreads, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {

        }
        return threadFactory.buildIndex();
    }

    private void handleDirectory(File dir, String path) {
        log.tracev("handling directory: {0}", dir);
        File[] files = dir.listFiles();
        assert files != null;
        for (File child : files) {
            String newPath = (path == null) ? child.getName() : (path + '/' + child.getName());

            if (child.isDirectory()) {
                handleDirectory(child, newPath);
            } else {
                try {
                    addToDiscovered(newPath, child.toURI().toURL());
                } catch (MalformedURLException e) {
                    log.errorv("Error loading file {0}", newPath);
                }
            }
        }
    }

    protected void addToDiscovered(String name, URL url) {
        if (name.endsWith(CLASS_FILE_EXTENSION)) {
            discoveredClasses.add(filenameToClassname(name));
            addToIndex(url);
        } else if (name.endsWith(BEANS_XML)) {
            discoveredBeansXmlUrls.add(url);
        }
    }

    /**
     * Convert a path to a class file to a class name
     */
    public static String filenameToClassname(String filename) {
        return filename.substring(0, filename.lastIndexOf(CLASS_FILE_EXTENSION)).replace('/', '.').replace('\\', '.');
    }

    public List<String> getDiscoveredClasses() {
        return discoveredClasses;
    }

    public List<URL> getDiscoveredBeansXmlUrls() {
        return discoveredBeansXmlUrls;
    }
}
