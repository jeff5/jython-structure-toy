package example.runtime;

import java.util.Map;

public interface PyType /* extends Representation */ {

    PyType getType();

    Map<Object, Object> getDict();

    /** Name of this type. */
    String getName();

    /**
     * Look for a name, returning the entry directly from the
     * dictionary. (No MRO in this toy version.)
     *
     * @param name to look up
     * @return dictionary entry or null
     */
    // Compare CPython _PyType_Lookup in typeobject.c
    Object lookup(String name);
}
