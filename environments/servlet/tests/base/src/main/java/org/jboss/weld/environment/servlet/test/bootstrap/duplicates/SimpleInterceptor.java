package org.jboss.weld.environment.servlet.test.bootstrap.duplicates;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;


@Interceptor
@SimpleBinding
public class SimpleInterceptor {

    @AroundInvoke
    public Object logMethodEntry(InvocationContext ctx) throws Exception {
        return ctx.proceed();
    }

}
