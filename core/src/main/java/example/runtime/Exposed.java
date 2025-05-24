// Copyright (c)2025 Jython Developers.
// Licensed to PSF under a contributor agreement.
package example.runtime;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotations that may be placed on elements of a Java class intended
 * as the implementation of a Python type, and that the the runtime will
 * look for when during the definition of a Python {@code type} or
 * {@code module} in Java.
 */
public interface Exposed {

    /**
     * Identify a Python instance method of a type or module defined in
     * Java and exposed to Python. The signature must be a supported
     * type for which coercions can be found for its parameters.
     * <p>
     * When found in the classes that define a built-in type, this
     * annotation results in a method definition, then a descriptor in
     * the dictionary of the type. When found in the class that defines
     * a built-in module, this annotation results in a method definition
     * in the module specification, and a bound method in the dictionary
     * of each module instance created from it.
     */
    @Documented
    @Retention(RUNTIME)
    @Target(METHOD)
    @interface PythonMethod {}
}
