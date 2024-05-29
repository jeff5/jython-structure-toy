package example.internal;

import example.runtime.Crafted;
import example.runtime.PyType;

/**
 * Class that may be used as a base for Python objects (but doesn't have
 * to be) to supply some universally needed methods and the type.
 */
abstract class AbstractPyObject implements Crafted {

    private PyType type;

    /**
     * Constructor specifying the Python type, as returned by
     * {@link #getType()}. As this is a base for the implementation of
     * all sorts of Python types, it needs to be told which one it is.
     *
     * @param type actual Python type being created
     */
    protected AbstractPyObject(PyType type) { this.type = type; }

    @Override
    public PyType getType() { return type; }


}
