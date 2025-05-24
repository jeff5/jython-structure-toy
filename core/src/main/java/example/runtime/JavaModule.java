package example.runtime;

import java.lang.invoke.MethodHandles.Lookup;

/**
 * Base class of built-in and extension modules written in Java.
 */
public abstract class JavaModule extends PyModule {

    /**
     * Construct the base {@code JavaModule}, initialising it from the
     * module definition, which is normally created during static
     * initialisation of the concrete class defining the module.
     * <p>
     * Methods defined in the module definition are added to the
     * dictionary in this phase.
     *
     * @param definition of the module
     */
    protected JavaModule(Definition definition) {
        super(definition.getName());
        definition.addMembers(this);
    }

    /**
     * Create a module definition by scanning a defining class for
     * exposed methods.
     *
     * @param name to assign the module
     * @param lookup access rights to its members
     * @return a module definition derived from the lookup class
     */
    public static Definition define(String name, Lookup lookup) {
        Class<?> c = lookup.lookupClass();
        if (JavaModule.class.isAssignableFrom(c)) {
            @SuppressWarnings("unchecked")
            Class<? extends JavaModule> definingClass =
                    (Class<? extends JavaModule>)c;
            return new ModuleDef<>(name, definingClass, lookup);
        } else {
            String msg = String.format(
                    "module %s (Java class %s) must extend JavaModule",
                    name, c.getName());
            throw new PyException(msg);
        }
    }

    /**
     * Create a module definition by scanning a defining class for
     * exposed methods.
     *
     * @param name to assign the module
     * @param definingClass of the module
     * @param lookup access rights to its members
     * @return a module definition derived from the defining class
     */
    public static <JM extends JavaModule> Definition define(String name,
            Class<JM> definingClass, Lookup lookup) {
        return new ModuleDef<JM>(name, definingClass, lookup);
    }

    /**
     * Interface specifying the actions required to create and populate
     * an instance of the module specified by a definition.
     */
    public interface Definition {
        /**
         * Name given to the module.
         *
         * @return the name
         */
        String getName();

        /**
         * Add members from this definition to the dictionary of a
         * module instance. Although a definition is created once, it
         * may populate multiple instances.
         *
         * @param module to populate
         */
        void addMembers(JavaModule module);
    }
}
