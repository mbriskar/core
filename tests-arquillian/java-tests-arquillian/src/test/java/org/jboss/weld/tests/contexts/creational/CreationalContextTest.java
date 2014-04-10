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
package org.jboss.weld.tests.contexts.creational;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.context.WeldCreationalContext;
import org.jboss.weld.context.api.ContextualInstance;
import org.jboss.weld.util.reflection.Reflections;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CreationalContextTest {

    @Deployment
    public static Archive<?> getDeployment() {
        return ShrinkWrap.create(BeanArchive.class).addPackage(CreationalContextTest.class.getPackage());
    }

    @Test
    public void testOnlyDependenciesThatRequireCleanupAreStoredInCreationalContext(BeanManager manager) {
        Bean<InjectedBean> bean = Reflections.cast(manager.getBeans(InjectedBean.class).iterator().next());
        CreationalContext<InjectedBean> cc = manager.createCreationalContext(bean);
        @SuppressWarnings("unused")
        InjectedBean instance = bean.create(cc);

        WeldCreationalContext<InjectedBean> wcc = (WeldCreationalContext<InjectedBean>) cc;
        assertEquals(4, wcc.getDependentInstances().size());

        @SuppressWarnings("serial")
        Set<Class<?>> expectedDependentInstanceClasses = new HashSet<Class<?>>() {
            {
                add(DependencyWithPreDestroy.class);
                add(ProductWithDisposer.class);
                add(Bravo.class);
                add(Delta.class);
            }
        };
        Set<Class<?>> actualDependentInstanceClasses = new HashSet<Class<?>>();
        for (ContextualInstance<?> dependency : wcc.getDependentInstances()) {
            actualDependentInstanceClasses.add(dependency.getInstance().getClass());
        }
        assertEquals(expectedDependentInstanceClasses, actualDependentInstanceClasses);
    }
}
