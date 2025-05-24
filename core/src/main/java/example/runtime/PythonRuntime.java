package example.runtime;

import example.internal.TypeFactoryImpl;

/**
 * Static instances of the implementation types.
 * <p>
 * Is this how we ought to bootstrap the implementation?
 */
public class PythonRuntime {

    public static final TypeFactory typeFactory =
            new TypeFactoryImpl();
}
