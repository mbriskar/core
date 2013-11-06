/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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

package org.jboss.weld.environment.servlet.test.bootstrap.duplicates;

import static org.jboss.weld.environment.servlet.test.util.Deployments.baseDeployment;
import static org.junit.Assert.assertTrue;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.environment.servlet.test.util.BeansXml;
import org.junit.Test;

/**
*
*/
public class DuplicateBeansXmlMergingTestBase {

    public static WebArchive deployment() {
        BeansXml beansXml = new BeansXml();
        beansXml.interceptors(SimpleInterceptor.class);
        WebArchive war = baseDeployment(beansXml).addClasses(DuplicateBeansXmlMergingTestBase.class, SimpleBinding.class, SimpleInterceptor.class);
        JavaArchive library = ShrinkWrap.create(JavaArchive.class, "library.jar").addAsManifestResource(beansXml, "beans.xml");
        // .addClasses(SimpleBinding.class, SimpleInterceptor.class);
        war.addAsLibrary(library); // error here
        return war;
    }

    @Test
    public void testDuplicatesInSingleFileAreNotRemoved() {
        assertTrue(true);
        // tests should not throw deployment error because of 2 definitions of the same interceptor
    }
}

