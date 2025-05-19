package example.runtime;

/**
 * Static instances of the implementation types.
 * <p>
 * Is this how we ought to bootstrap the implementation?
 */
public class PythonRuntime {

    public static final TypeFactoryImpl typeFactory =
            new TypeFactoryImpl();
}
