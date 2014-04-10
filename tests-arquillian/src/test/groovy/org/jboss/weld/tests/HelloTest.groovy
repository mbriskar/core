package org.jboss.weld.tests;

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

import javax.inject.Inject

import org.jboss.arquillian.container.test.api.Deployment
import org.jboss.arquillian.junit.Arquillian
import org.jboss.shrinkwrap.api.ArchivePaths
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.asset.EmptyAsset
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(Arquillian.class)
class HelloTest {
    @Inject
    private MyExample hello

    @Deployment
    static WebArchive war() {
        ShrinkWrap.create(WebArchive.class) // to managed depdendencies
                //.addAsLibraries(JarLocation.jarLocation(GroovyObject.class)) // groovy-all
                .addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml")) // cdi on
                .addClasses(MyExample.class) // app classes
    }

    @Test
    void hello() {
        assertEquals hello.hi(),"hi"
    }
}
