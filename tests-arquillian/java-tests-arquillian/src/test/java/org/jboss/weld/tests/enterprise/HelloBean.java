/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.tests.enterprise;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.BeanManager;

@SuppressWarnings("serial")
@Stateful
@SessionScoped
// This annotation does not work on AS7/WildFly8 anyway
// @CacheConfig(idleTimeoutSeconds = 1)
public class HelloBean implements IHelloBean, Serializable {
    @Resource(mappedName = "java:comp/BeanManager")
    private BeanManager beanManager;

    public String sayHello() {
        return "hello";
    }

    public String sayGoodbye() {
        return beanManager.getELResolver() != null ? "goodbye" : "error";
    }

    @Remove
    public void remove() {
    }
}
