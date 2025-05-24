package example.runtime;

import example.internal.ImplUtil;

/**
 * Useful constants, minor types and methods for those implementing
 * extension modules.
 */
public class PyUtil {
    /**
     * Convenient default toString implementation that tries __str__, if
     * defined, but always falls back to something. Use as: <pre>
     * public String toString() { return PyUtil.defaultToString(this); }
     * </pre>
     *
     * @param o object to represent
     * @return a string representation
     */
    public static String defaultToString(Object o) {
        if (o == null)
            return "null";
        else {
            PyType t = null;
            try {
                t = PythonRuntime.typeFactory.of(o);
                Object v = t.lookup("__str__");
                if (v instanceof PyMethodDescr md) {
                    v = md.__get__(o, null);
                }
                if (v instanceof PyJavaFunction f) {
                    Object res = f.getHandle().invoke(EMPTY_ARRAY);
                    return res.toString();
                }
            } catch (Throwable e) {
                System.err.println(e);
            }

            // Even object.__str__ not working.
            if (t != null) {
                // Got a Python type at all?
                return "<" + ImplUtil.toAt(o) + ">";
            } else {
                // Maybe during start-up. Fall back to Java.
                Class<?> c = o.getClass();
                String name = c.isAnonymousClass() ? c.getName()
                        : c.getSimpleName();
                return "<" + name + " object>";
            }
        }
    }
    /** Empty (zero-length) array of {@code Object}. */
    static final Object[] EMPTY_ARRAY = new Object[0];
}
