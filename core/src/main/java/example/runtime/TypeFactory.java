package example.runtime;

import java.lang.invoke.MethodHandles.Lookup;

public interface TypeFactory {

    /**
     * Map a Java class to the Python {@code type} object that gives
     * Python semantics to instances of the class. In the toy
     * implementation, type enquiry does not bring about type creation.
     *
     * @param c class on which operations are required
     * @return {@code type} providing Python semantics
     */
    PyType fromClass(Class<?> c);

    /**
     * Get the Python type of the given object {@code obj}. The Java
     * class of {@code obj} will normally have been initialised, since
     * an instance exists.
     *
     * @param obj to inspect
     * @return the Python type of {@code obj}
     */
    PyType of(Object obj);

    /**
     * Add a Python type object to the registry for the specified lookup
     * class.
     *
     * @param name of the type in Python
     * @param lookup loan of access rights
     * @return registered type object
     */
    PyType register(String name, Lookup lookup);

    /**
     * Add a Python type object to the registry for the specified
     * representation class.
     *
     * @param name of the type in Python
     * @param javaClass representation class
     * @param lookup loan of access rights
     * @return registered type object
     */
    PyType register(String name, Class<?> javaClass, Lookup lookup);
}
