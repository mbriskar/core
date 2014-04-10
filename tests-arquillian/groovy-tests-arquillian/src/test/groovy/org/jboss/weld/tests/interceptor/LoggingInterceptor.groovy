package org.jboss.weld.tests.interceptor

import javax.interceptor.AroundInvoke
import javax.interceptor.Interceptor
import javax.interceptor.InvocationContext

@Interceptor
@Log
class LoggingInterceptor {
    static boolean intercepted = false;
    @AroundInvoke
    public Object logMethodEntry(InvocationContext ctx) throws Exception {
        intercepted=true;
        return ctx.proceed();
    }
}