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
package org.jboss.weld.util;

import org.jboss.weld.exceptions.IllegalArgumentException;
import static org.jboss.weld.logging.messages.ValidatorMessage.ARGUMENT_NULL;

/**
 *
 * @author Jozef Hartinger
 *
 */
public class Preconditions {

    private Preconditions() {
    }

    /**
     * Throws {@link IllegalArgumentException} with an appropriate message if the reference is null.
     *
     * @param reference the reference to be checked
     * @param argumentName name of the argument that is being checked. The name used in the error message.
     */
    public static void checkArgumentNotNull(Object reference, String argumentName) {
        if (reference == null) {
            throw new IllegalArgumentException(ARGUMENT_NULL, argumentName);
        }
    }
}
