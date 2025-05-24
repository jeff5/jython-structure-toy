package example.runtime;

import java.lang.invoke.MethodHandles;

public abstract class PyModule implements WithDict {

    /** Name of this module. Not {@code null}. **/
    final String name;

    /** Dictionary (globals) of this module. Not {@code null}. **/
    final PyDict dict;

    /**
     * Construct an instance of the named module.
     *
     * @param name of module
     */
    PyModule(String name) {
        this.name = name;
        this.dict = new PyDict();
    }

    @Exposed.PythonMethod
    static Object __str__(PyModule self) {
        String bi = self instanceof JavaModule ? " (built-in)" : "";
        return String.format("<module '%s'%s>", self.name, bi);
    }

    @Override
    public PyType getType() { return TYPE; }

    @Override
    public PyDict getDict() { return dict; }

    /**
     * Initialise the module instance. The main action will be to add
     * entries to {@link #dict}. These become the members (globals) of
     * the module. It must be separate from the constructor so that
     * module reload (which is re-execution) is possible.
     */
    public void exec() {}

    @Override
    public String toString() { return PyUtil.defaultToString(this); }

    public static final PyType TYPE =
            PythonRuntime.typeFactory.register("module", MethodHandles.lookup());
}
