package example.core;

public interface Representation {
    /**
     * Get the Python type of the object <i>given that</i> this is the
     * representation object for it.
     *
     * @param x subject of the enquiry
     * @return {@code type(x)}
     */
    PyType pythonType(Object x);

    /**
     * A base Java class representing instances of the related Python
     * {@code type} associated with this {@code Representation}.
     *
     * @return base class of the representation
     */
    Class<?> javaClass();
}
