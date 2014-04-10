package org.jboss.weld.tests;

import org.jboss.arquillian.container.spi.client.container.DeploymentExceptionTransformer;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;

/**
 * Arquillian loadable extension.
 *
 * @author Aslak Knutsen
 * @author Stuart Douglas
 * @author Martin Kouba
 */
public class WeldExtension implements LoadableExtension {

    // TODO the managed container class did not change so far, but will likely change soon
    private static final String MANAGED_CONTAINER_CLASS = "org.jboss.as.arquillian.container.managed.ManagedDeployableContainer";

    private static final String MANAGED_CONTAINER_DEFAULT_EXCEPTION_TRANSFORMER_CLASS = "org.jboss.as.arquillian.container.ExceptionTransformer";

    public void register(ExtensionBuilder builder) {

        builder.service(AuxiliaryArchiveAppender.class, CategoryArchiveAppender.class);

        if (Validate.classExists(MANAGED_CONTAINER_CLASS)) {
            // Override the default NOOP exception transformer
            builder.override(DeploymentExceptionTransformer.class, getDefaultExceptionTransformerClass(),
                    WildFly8DeploymentExceptionTransformer.class);
        } else {
            builder.service(DeploymentExceptionTransformer.class, WeldExceptionTransformer.class);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<DeploymentExceptionTransformer> getDefaultExceptionTransformerClass() {
        try {
            return (Class<DeploymentExceptionTransformer>) Class.forName(MANAGED_CONTAINER_DEFAULT_EXCEPTION_TRANSFORMER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Default exception transformer class not found: "
                    + MANAGED_CONTAINER_DEFAULT_EXCEPTION_TRANSFORMER_CLASS);
        }
    }
}
