package org.jboss.weld.environment.servlet.test.bootstrap.duplicates;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

@Target({ TYPE, METHOD })
@Retention(RUNTIME)
@Documented
@InterceptorBinding
public @interface SimpleBinding {

}