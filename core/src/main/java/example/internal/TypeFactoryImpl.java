package example.internal;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import example.runtime.PyObject;
import example.runtime.PyType;
import example.runtime.TypeFactory;
import example.runtime.Exposed.PythonMethod;

public class TypeFactoryImpl implements TypeFactory {

    /** Mapping of Java class to PyType representing it. */
    private Map<Class<?>, PyTypeImpl> reg = new HashMap<>();

    /** Construct a type factory. */
    public TypeFactoryImpl() {}

    /**
     * Map a Java class to the Python {@code type} object that gives
     * Python semantics to instances of the class. In the toy
     * implementation, type enquiry does not bring about type creation.
     *
     * @param c class on which operations are required
     * @return {@code type} providing Python semantics
     */
    @Override
    public PyType fromClass(Class<?> c) {
        do {
            PyTypeImpl t = reg.get(c);
            if (t != null) {
                return t;
            } else {
                c = c.getSuperclass();
            }
        } while (c != null);
        // c is Object or an interface
        return PyObject.TYPE;
    }

    /**
     * Get the Python type of the given object {@code obj}. The Java
     * class of {@code obj} will normally have been initialised, since
     * an instance exists.
     *
     * @param obj to inspect
     * @return the Python type of {@code obj}
     */
    @Override
    public PyType of(Object obj) {
        return fromClass(obj.getClass());
    }

    /**
     * Add a Python type object to the registry for the specified lookup
     * class.
     *
     * @param name of the type in Python
     * @param lookup loan of access rights
     * @return registered type object
     */
    @Override
    public PyTypeImpl register(String name, Lookup lookup) {
        return register(name, lookup.lookupClass(), lookup);
    }

    /**
     * Add a Python type object to the registry for the specified
     * representation class.
     *
     * @param name of the type in Python
     * @param javaClass representation class
     * @param lookup loan of access rights
     * @return registered type object
     */
    @Override
    public PyTypeImpl register(String name, Class<?> javaClass,
            Lookup lookup) {
        PyTypeImpl type = new PyTypeImpl(name, javaClass);
        Class<?> defnClass = lookup.lookupClass();
        for (Method m : defnClass.getDeclaredMethods()) {
            PythonMethod pm =
                    m.getDeclaredAnnotation(PythonMethod.class);
            if (pm != null) {
                // m is annotated for exposure.
                type.addMethod(m, lookup);
            }
        }
        reg.put(javaClass, type);
        return type;
    }
}
