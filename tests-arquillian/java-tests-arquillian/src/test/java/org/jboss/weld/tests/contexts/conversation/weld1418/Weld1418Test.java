package org.jboss.weld.tests.contexts.conversation.weld1418;

import static junit.framework.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.tests.category.Integration;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 *
 */
@Category(Integration.class)
@RunWith(Arquillian.class)
public class Weld1418Test {

    public static final String VALUE = "foo";

    @Deployment
    public static WebArchive deployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
            .addClasses(Servlet.class, SomeBean.class)
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @ArquillianResource
    private URL baseUrl;

    @Test
    @RunAsClient
    public void testTwoConcurrentInitialRequestsInSameSessionButDifferentConversations() throws Exception {
        CookieHandler.setDefault(new CookieManager());
        String cid;
        HttpURLConnection conn = (HttpURLConnection) new URL(baseUrl + "servlet/startConversation?value=" + VALUE).openConnection();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            try {
                cid = in.readLine();
                makeRequest(new URL(baseUrl + "servlet/shortRequest"));

                while (in.readLine() != null) { // just wait for the first request to end
                }
            } finally {
                in.close();
            }
        } finally {
            conn.disconnect();
        }

        String value = makeRequest(new URL(baseUrl + "servlet/getValue?cid=" + cid));
        assertEquals(VALUE, value);
    }

    @Test
    @RunAsClient
    public void testSecondRequestInSameConversationWhileFirstRequestStillActive() throws Exception {
        CookieHandler.setDefault(new CookieManager());
        String cid;
        HttpURLConnection conn = (HttpURLConnection) new URL(baseUrl + "servlet/startConversation?sleep=500&value=" + VALUE).openConnection();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            try {
                cid = in.readLine();

                String value = makeRequest(new URL(baseUrl + "servlet/getValue?cid=" + cid));

                while (in.readLine() != null) { // just wait for the first request to end
                }

                assertEquals(VALUE, value);
            } finally {
                in.close();
            }
        } finally {
            conn.disconnect();
        }
    }


    private String makeRequest(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            try {
                return in.readLine();
            } finally {
                in.close();
            }
        } finally {
            conn.disconnect();
        }
    }
}
