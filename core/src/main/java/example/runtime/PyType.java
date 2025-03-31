package example.runtime;

import static example.internal.ClassShorthand.OA;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import example.core.PyException;
import example.internal.PyBaseObject;
import example.internal.PyMethodDescr;
import example.runtime.Exposed.PythonMethod;

public class PyType extends Representation  implements WithDict {

    /** Mapping of Java class to PyType representing it. */
    private static Map<Class<?>, PyType> reg = new HashMap<>();

    private final String name;

    private HashMap<Object, Object> dict = new HashMap<>();

    /** Construct a type with the given representation class. */
    PyType(String name, Class<?> javaClass) {
        super(javaClass);
        this.name = name;
    }

    @Override
    public PyType getType() { return TYPE; }

    @Override
    public Map<Object, Object> getDict() { return dict; }

    /** Name of this type. */
    public String getName() { return name; }

    /**
     * Map a Java class to the Python {@code type} object that gives
     * Python semantics to instances of the class. In the toy
     * implementation, type enquiry does not bring about type creation.
     *
     * @param c class on which operations are required
     * @return {@code type} providing Python semantics
     */
    public static PyType fromClass(Class<?> c) {
        do {
            PyType t = reg.get(c);
            if (t != null) {
                return t;
            } else {
                c = c.getSuperclass();
            }
        } while (c != null);
        // c is Object or an interface
        return PyBaseObject.TYPE;
    }

    @Override
    public PyType pythonType(Object x) {
        return this;
    }

    /**
     * Get the Python type of the given object {@code obj}. The Java
     * class of {@code obj} will normally have been initialised, since
     * an instance exists.
     *
     * @param obj to inspect
     * @return the Python type of {@code obj}
     */
    public static PyType of(Object obj) {
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
    public static PyType register(String name, Lookup lookup) {
        return PyType.register(name, lookup.lookupClass(), lookup);
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
    public static PyType register(String name, Class<?> javaClass,
            Lookup lookup) {
        PyType type = new PyType(name, javaClass);
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

    /**
     * Add a Python instance method to the type as a
     * {@link PyMethodDescr} in the type's dictionary. The reflective
     * description {@code m} of the method gives us the parameter types
     * with which it was declared (in Java), but these must be adapted
     * so that the descriptor holds a method handle with signature
     * {@code (O,O[])O}.
     *
     * @param m method to describe
     * @param lookup access rights to defining class
     */
    private void addMethod(Method m, Lookup lookup) {
        String mName = m.getName();
        // Declared static with explicit self type S (in toy).
        assert Modifier.isStatic(m.getModifiers());

        try {
            // Signature of handle is to be (O,O[])O
            /*
             * In real life we would need run-time checks and
             * conversion, but this is just a toy.
             */
            MethodHandle mh = lookup.unreflect(m);
            MethodType mt = mh.type();
            int n = mt.parameterCount();
            assert n > 0;
            assert mt.parameterType(0).isAssignableFrom(javaClass);
            if (n == 1) {
                // Signature is currently (S)T
                // Add an ignored array argument (no args at run-time).
                mh = MethodHandles.dropArguments(mh, 1, OA);
            } else {
                // Signature is currently (S,A,B,...)T
                // We shall receive an array we spread to the args.
                mh = mh.asSpreader(OA, n - 1);
            }
            mh = mh.asType(MT.METHOD);
            PyMethodDescr descr = new PyMethodDescr(this, mName, mh);
            dict.put(mName, descr);
        } catch (IllegalAccessException e) {
            String msg = String.format(
                    "Cannot expose method %s.%s: due to %s", name,
                    mName, e);
            throw new PyException(msg, e);
        }
    }

    /**
     * Look for a name, returning the entry directly from the
     * dictionary. (No MRO in this toy version.)
     *
     * @param name to look up
     * @return dictionary entry or null
     */
    // Compare CPython _PyType_Lookup in typeobject.c
    public Object lookup(String name) { return dict.get(name); }

    public static PyType TYPE =
            PyType.register("type", MethodHandles.lookup());

    @Override
    public String toString() { return "<class '" + name + "'>"; }
}
