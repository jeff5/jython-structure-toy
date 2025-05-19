package example.internal;

import example.runtime.PyTypeImpl;
import example.runtime.PyUtil;

/**
 * Useful constants, minor types and methods for those implementing
 * Jython itself. These are unsafe for use or not thought stable enough
 * to expose as API.
 */
/*
 * If we want to make something here into API, we may either provide an
 * alias to it in PyUtil, or move it there. We inherit PyUtil here so
 * that we may do that without breakage. If X migrates to PyUtil,
 * ImplUtil.X in our code still references it.
 */
public class ImplUtil extends PyUtil {
    /**
     * A string along the lines "T object at 0xhhh", where T is the type
     * of {@code o}. This is for creating default {@code __repr__}
     * implementations seen around the code base and containing this
     * form. By implementing it here, we encapsulate the problem of
     * qualified type name and what "address" or "identity" should mean.
     *
     * @param o the object (not its type)
     * @return string denoting {@code o}
     */
    public static String toAt(Object o) {
        // For the time being type name means:
        String typeName = PyTypeImpl.of(o).getName();
        return String.format("%s object at %#x", typeName, id(o));
    }

    /**
     * Return the unique numerical identity of a given Python object. No
     * two objects have the same {@code id()} at the same time. By
     * implementing it here, we encapsulate the problem of qualified
     * type name and what "address" or "identity" should mean.
     *
     * @param o the object
     * @return the Python {@code id(o)}
     */
    public static int id(Object o) {
        // For the time being identity means:
        return System.identityHashCode(o);
    }
}
