package example.internal;

import java.lang.invoke.MethodHandles;

import example.runtime.Exposed;
import example.runtime.PyTypeImpl;
import example.runtime.PythonRuntime;

/**
 * The Python {@code object} object. In the toy implementation we do not
 * have MRO. If we did, this {@link PyBaseObject#TYPE} would be at the
 * end of every type's except its own.
 */
public class PyBaseObject {

    private PyBaseObject() {}

    // Special methods ------------------------------------------------

    /*
     * Methods must be static with a "self" argument of type Object so
     * that method handles copied from the slots of "object" function
     * correctly in the type slots of Python objects.
     *
     * It follows that operations performed here must be feasible for
     * any Python object.
     */

    @Exposed.PythonMethod
    static Object __str__(Object self) {
        return "<" + ImplUtil.toAt(self) + ">";
    }

    /** The type object of {@code object} objects. */
    public static final PyTypeImpl TYPE =
            PythonRuntime.typeFactory.register("object",
            Object.class, MethodHandles.lookup());
}
