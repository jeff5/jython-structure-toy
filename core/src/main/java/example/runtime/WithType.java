package example.runtime;

/**
 * A crafted Python object implementation is one where each instance
 * reports an explicit type.
 */
public interface WithType {
    PyType getType();
}
