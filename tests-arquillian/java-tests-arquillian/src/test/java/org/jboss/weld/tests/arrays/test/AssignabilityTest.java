package org.jboss.weld.tests.arrays.test;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.BeanArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.weld.tests.arrays.ArrayProducer;
import org.jboss.weld.tests.arrays.Bar;
import org.jboss.weld.tests.arrays.Foo;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 *
 */
@RunWith(Arquillian.class)
public class AssignabilityTest {

    @Inject
    private BeanManager beanManager;

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(BeanArchive.class)
            .addClasses(Result.class);
    }

    @Test
    public void testAssignabilityOfParameterizedTypeWithTypeVariablesToParameterizedTypeWithWildcards()
    {
        Set<Bean<Result<? extends Throwable, ? super Exception>>> beans = getBeans(new TypeLiteral<Result<? extends Throwable, ? super Exception>>() {
        });
        Assert.assertEquals(1, beans.size());
        Assert.assertTrue(rawTypeSetMatches(beans.iterator().next().getTypes(), Result.class, Object.class));
    }

    @Test
    public void testAssignabilityOfParameterizedTypeWithTypeVariablesToParameterizedTypeWithWildcards2()
    {
        Set<Bean<Result<? extends Exception, ? super Exception>>> beans = getBeans(new TypeLiteral<Result<? extends Exception, ? super Exception>>(){});
        Assert.assertEquals(1, beans.size());
        Assert.assertTrue(rawTypeSetMatches(beans.iterator().next().getTypes(), Result.class, Object.class));
    }

    public <T> Set<Bean<T>> getBeans(TypeLiteral<T> type, Annotation... bindings) {
        return (Set)beanManager.getBeans(type.getType(), bindings);
    }


    public boolean rawTypeSetMatches(Set<Type> types, Class<?>... requiredTypes)
    {
        List<Class<?>> typeList = new ArrayList<Class<?>>();
        typeList.addAll(Arrays.asList(requiredTypes));
        for (Type type : types)
        {
            if (type instanceof Class<?>)
            {
                typeList.remove(type);
            }
            else if (type instanceof ParameterizedType)
            {
                typeList.remove(((ParameterizedType) type).getRawType());
            }
        }
        return typeList.size() == 0;
    }

}