package example.internal;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import example.core.PyType;
import example.runtime.Crafted;
import example.runtime.Exposed;
import example.runtime.MT;
import example.runtime.PyTypeImpl;
import example.runtime.PyUtil;

public class PyMethodDescr implements Crafted {

    public final PyType objclass;
    public final String name;
    /** Handle on {@code __call__} always of type {@link MT#METHOD} */
    public final MethodHandle method;

    public PyMethodDescr(PyType objclass, String name,
            MethodHandle method) {
        this.objclass = objclass;
        this.name = name;
        assert method.type() == MT.METHOD;
        this.method = method;
    }

    @Exposed.PythonMethod
    static Object __str__(PyMethodDescr self) {
        return String.format("<method '%.50s' of '%.100s' objects>",
                self.name, self.objclass.getName());
    }

    @Override
    public PyTypeImpl getType() { return TYPE; }

    /**
     * Return the unbound handle contained in this descriptor.
     *
     * @return corresponding handle
     */
    public MethodHandle getHandle() throws Throwable {
        return method;
    }

    /**
     * Return the described method, bound to {@code obj} as its "self"
     * argument, or if {@code obj==null}, return this descriptor.
     *
     * @param obj target (self) of the method, or {@code null}
     * @param type ignored
     * @return method bound to {@code obj} or this descriptor.
     * @throws Throwable on other errors while chasing the MRO
     */
    // Compare CPython method_get in descrobject.c
    public Object __get__(Object obj, PyType type) throws Throwable {
        if (obj == null)
            // Return the descriptor itself.
            return this;
        else {
            // Return a callable binding the method and the target
            // check(obj);
            return PyJavaFunction.from(this, obj);
        }
    }

    @Override
    public String toString() { return PyUtil.defaultToString(this); }

    public static final PyTypeImpl TYPE = PyTypeImpl
            .register("method_descriptor", MethodHandles.lookup());
}
