package org.jboss.weld.environment.servlet.test.bootstrap.duplicates;

import static org.jboss.weld.environment.servlet.test.util.JettyDeployments.JETTY_ENV;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DuplicateBeansXmlMergingTest extends DuplicateBeansXmlMergingTestBase {
    @Deployment
    public static WebArchive deployment() {
        return DuplicateBeansXmlMergingTestBase.deployment().addAsWebInfResource(JETTY_ENV, "jetty-env.xml");
    }
}