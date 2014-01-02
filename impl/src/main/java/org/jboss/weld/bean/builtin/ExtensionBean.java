/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.bean.builtin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.annotated.enhanced.EnhancedAnnotatedType;
import org.jboss.weld.bean.BeanIdentifiers;
import org.jboss.weld.bean.StringBeanIdentifier;
import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.logging.BeanLogger;
import org.jboss.weld.manager.BeanManagerImpl;
import org.jboss.weld.util.Proxies;

/**
 * @author pmuir
 */
public class ExtensionBean extends AbstractBuiltInBean<Extension> {

    private final AnnotatedType<Extension> annotatedType;
    private final Metadata<Extension> instance;
    private final boolean passivationCapable;
    private final boolean proxiable;

    public ExtensionBean(BeanManagerImpl manager, EnhancedAnnotatedType<Extension> clazz, Metadata<Extension> instance) {
        super(new StringBeanIdentifier(BeanIdentifiers.forExtension(clazz)), manager, clazz.getJavaClass());
        check(clazz);
        this.annotatedType = clazz.slim();
        this.instance = instance;
        this.passivationCapable = clazz.isSerializable();
        this.proxiable = Proxies.isTypeProxyable(clazz.getBaseType(), manager.getServices());
    }

    private void check(EnhancedAnnotatedType<Extension> clazz) {
        for (AnnotatedField<?> field : clazz.getFields()) {
            Member member = field.getJavaMember();
            String memberString = member.toString();
            String clazzString = clazz.getBaseType().toString();
            if (Modifier.isPublic(member.getModifiers()) && !Modifier.isStatic(member.getModifiers())) {
                // warn when an extension has a non-static public field
                BeanLogger.LOG.extensionWithNonStaticPublicField(clazz.getBaseType(), member);
            }
        }
    }

    public Set<Type> getTypes() {
        return annotatedType.getTypeClosure();
    }

    @Override
    public boolean isProxyable() {
        return proxiable;
    }

    @Override
    public boolean isPassivationCapableBean() {
        return passivationCapable;
    }

    @Override
    public Extension create(CreationalContext<Extension> creationalContext) {
        return instance.getValue();
    }

    public void destroy(Extension instance, CreationalContext<Extension> creationalContext) {
        // No-op
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ApplicationScoped.class;
    }

    @Override
    public String toString() {
        return "Extension [" + getType().toString() + "] with qualifiers [@Default]; " + instance.getLocation();
    }
}
