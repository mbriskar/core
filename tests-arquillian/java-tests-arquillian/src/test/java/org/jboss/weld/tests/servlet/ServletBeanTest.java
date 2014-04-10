package org.jboss.weld.tests.servlet;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class ServletBeanTest {

    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(BeanArchive.class)
                .addPackage(ServletBeanTest.class.getPackage());
    }

    @Test
    // WELD-492
    public void testImplementsServlet(Foo foo) {
        assertNotNull(foo);
    }

    @Test
    // WELD-492
    public void testImplementsFilter(Bar bar) {
        assertNotNull(bar);
    }

    @Test
    // WELD-492
    public void testImplementsServletContextListener(Baz baz) {
        assertNotNull(baz);
    }

    @Test
    // WELD-492
    public void testImplementsHttpSessionListener(Qux qux) {
        assertNotNull(qux);
    }

    @Test
    // WELD-492
    public void testImplementsServletREquestListener(Corge corge) {
        assertNotNull(corge);
    }

}
