// Copyright (c)2024 Jython Developers.
// Licensed to PSF under a contributor agreement.
package example.core;

import java.lang.reflect.Constructor;

import example.internal.PyTypeImpl;
import example.runtime.JavaModule;
import example.runtime.PyException;
import example.runtime.PyInt;
import example.runtime.PyJavaFunction;
import example.runtime.PyMethodDescr;
import example.runtime.PyObject;
import example.runtime.PyType;

/**
 * An {@code Interpreter} is the wider context for execution, mainly
 * defining import state. It does not otherwise confine threads or
 * objects (somewhat unlike CPython).
 */
public class Interp {
    /** Convert {@code o} to a {@code str}. */
    public Object str(Object o) {
        return invoke("__str__", o);
    }

    /** Compute -u */
    public Object neg(Object u) {
        return invoke("__neg__", u);
    }

    /** Compute {@code u + v}. */
    public Object add(Object u, Object v) {
        // We should consult __radd__ here too, but not in the toy.
        return invoke("__add__", u, v);
    }

    public Object call(Object o, Object... args) {
        /*
         * This looks weird but all our invocations (in the toy) expect
         * an array containing the arguments expected by the called
         * method. The fact that the (single) argument expected by
         * __call__ is itself an array, does not exempt it.
         */
        return invoke("__call__", o, new Object[] {args});
        /*
         * If we had known o was in fact a PyJavaFunction f we could
         * have short-circuited to invoke its handle directly, but in
         * general we only know it is an object of a type that (we hope)
         * defines a the __call_ method.
         */
    }

    private static Object invoke(String name, Object o,
            Object... args) {
        try {
            PyType t = PyTypeImpl.of(o);
            Object v = t.lookup(name);
            // If the retrieved object is a descriptor, bind it.
            if (v instanceof PyMethodDescr md) {
                v = md.__get__(o, null);
            }
            if (v instanceof PyJavaFunction f) {
                return f.getHandle().invoke(args);
            } else {
                String msg = String.format("(%s).%s is not callable", o,
                        name);
                throw new PyException(msg);
            }
        } catch (Throwable t) {
            throw PyException.wrapped(t);
        }
    }

    /**
     * Create an instance in this interpreter of the given class, which
     * must extend {@link JavaModule}. This will fail if a parameterless
     * constructor for {@code cls} is not accessible to Jython, for
     * example if the package of cls is not exported from its module.
     *
     * @param <JM> the actual type
     * @param cls from which to create an instance
     * @return the instance
     */
    public <JM extends JavaModule> JM importJavaModule(Class<JM> cls) {
        try {
            Constructor<JM> cons = cls.getConstructor();
            JM module = cons.newInstance();
            // Should add to sys.path here (in the sys of this module).
            // And then execute the body of the new instance.
            module.exec();
            return module;
        } catch (ReflectiveOperationException roe) {
            String msg = String.format(
                    "Could not create module from %s", cls.getName());
            throw new PyException(msg, roe);
        }
    }

    /**
     * Add a module instance to this interpreter. The interpreter will
     * complete the instance's initialisation as a Python module,
     * populating the module dictionary by executing the body.
     *
     * @param module to execute
     */
    public void addModule(JavaModule module) {
        // Should add to sys.path here (in the sys of this interpreter).
        // And then execute the body of the new instance.
        module.exec();
    }

    /**
     * Call the method named on the object.
     *
     * @param o target object
     * @param methodName to find the method by
     * @param args to supply to method (when bound)
     * @return result of call
     */
    public Object callMethod(Object o, String methodName,
            Object... args) {
        /*
         * We take a narrow view of acceptable descriptors for now, as
         * we do not yet have a __getattribute__.
         */
        return invoke(methodName, o, args);
    }

    /*
     * We reference the type objects of some built-in types to ensure
     * they are registered. Order is delicate.
     */
    static PyType TYPE_TYPE = PyTypeImpl.TYPE; // First!
    static PyType METHOD_DESCR_TYPE = PyMethodDescr.TYPE;
    static PyType JAVA_FUNCTION_TYPE = PyJavaFunction.TYPE;
    static PyType OBJECT_TYPE = PyObject.TYPE;
    static PyType INT_TYPE = PyInt.TYPE;
}
