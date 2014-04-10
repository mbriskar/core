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
package org.jboss.weld.tests.interceptors.ejb;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Timer;
import javax.enterprise.inject.spi.InterceptionType;
import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.bean.SessionBean;
import org.jboss.weld.bean.interceptor.InterceptorBindingsAdapter;
import org.jboss.weld.ejb.spi.InterceptorBindings;
import org.jboss.weld.manager.BeanManagerImpl;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class EnterpriseBeanInterceptionTest {
    @Deployment
    public static Archive<?> deploy() {
        return ShrinkWrap.create(BeanArchive.class)
                .intercept(Goalkeeper.class, Defender.class, Referee.class)
                .addPackage(EnterpriseBeanInterceptionTest.class.getPackage());
    }

    @Inject
    private BeanManagerImpl beanManager;

    @Test
    public void testX() {
    }

    // @Test -- impl details class
    public void testInterceptors() throws Exception {
        SessionBean<Ball> ballSessionBean = (SessionBean<Ball>) beanManager.getBeans(Ball.class).iterator().next();
        InterceptorBindings interceptorBindings = new InterceptorBindingsAdapter(beanManager.getInterceptorModelRegistry().get(ballSessionBean.getAnnotated()));
        List<javax.enterprise.inject.spi.Interceptor> interceptors =
                new ArrayList<javax.enterprise.inject.spi.Interceptor>(interceptorBindings.getAllInterceptors());

        Assert.assertEquals(3, interceptors.size());
        List<Class<?>> expectedInterceptors = Arrays.<Class<?>>asList(Goalkeeper.class, Defender.class, Referee.class);
        Assert.assertTrue(expectedInterceptors.contains(interceptors.get(0).getBeanClass()));
        Assert.assertTrue(expectedInterceptors.contains(interceptors.get(1).getBeanClass()));
        Assert.assertTrue(expectedInterceptors.contains(interceptors.get(2).getBeanClass()));


        Assert.assertEquals(0, interceptorBindings.getMethodInterceptors(InterceptionType.AROUND_TIMEOUT, ballSessionBean.getType().getMethod("shoot")).size());
        Assert.assertEquals(1, interceptorBindings.getMethodInterceptors(InterceptionType.AROUND_INVOKE, ballSessionBean.getType().getMethod("shoot")).size());
        Assert.assertEquals(Goalkeeper.class, interceptorBindings.getMethodInterceptors(InterceptionType.AROUND_INVOKE, ballSessionBean.getType().getMethod("shoot")).get(0).getBeanClass());

        Assert.assertEquals(0, interceptorBindings.getMethodInterceptors(InterceptionType.AROUND_TIMEOUT, ballSessionBean.getType().getMethod("pass")).size());
        Assert.assertEquals(1, interceptorBindings.getMethodInterceptors(InterceptionType.AROUND_INVOKE, ballSessionBean.getType().getMethod("pass")).size());
        Assert.assertEquals(Defender.class, interceptorBindings.getMethodInterceptors(InterceptionType.AROUND_INVOKE, ballSessionBean.getType().getMethod("pass")).get(0).getBeanClass());

        Method finishGameMethod = ballSessionBean.getType().getMethod("finishGame", Timer.class);
        Assert.assertEquals(0, interceptorBindings.getMethodInterceptors(InterceptionType.AROUND_INVOKE, finishGameMethod).size());
        Assert.assertEquals(1, interceptorBindings.getMethodInterceptors(InterceptionType.AROUND_TIMEOUT, finishGameMethod).size());
        Assert.assertEquals(Referee.class, interceptorBindings.getMethodInterceptors(InterceptionType.AROUND_TIMEOUT, finishGameMethod).get(0).getBeanClass());
    }
}
