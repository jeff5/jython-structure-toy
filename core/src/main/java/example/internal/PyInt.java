package example.internal;

import java.lang.invoke.MethodHandles;

import example.runtime.Exposed;
import example.runtime.PyTypeImpl;

public class PyInt {

    @Exposed.PythonMethod
    static Object __str__(Integer o) { return o.toString(); }

    @Exposed.PythonMethod
    static Object __neg__(Integer o) { return -o; }

    @Exposed.PythonMethod
    static Object __add__(Integer u, Object v) {
        return u + (Integer)v;
    }

    public static final PyTypeImpl TYPE = PyTypeImpl.register("int",
            Integer.class, MethodHandles.lookup());
}
