// Copyright (c)2024 Jython Developers.
// Licensed to PSF under a contributor agreement.
package example.runtime;

import static example.internal.ClassShorthand.OA;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import example.runtime.Exposed.PythonMethod;

/**
 * A {@code ModuleDef} is a definition from which instances of a module
 * may be populated.
 */
public class ModuleDef<JM extends JavaModule>
        implements JavaModule.Definition {

    /** Name of the module. */
    final String name;

    /** The Java class defining instances of the module. */
    final Class<JM> definingClass;

    /**
     * Definitions for the members that appear in the dictionary of
     * instances of the module named. Instances receive members by copy,
     * by binding to the module instance (descriptors), or by reference
     * (if immutable).
     */
    private final MethodDef[] methods;

    /**
     * Create a definition for the module, largely by introspection on
     * the class and by forming {@code MethodHandle}s on discovered
     * attributes.
     *
     * @param moduleName of the module (e.g. "sys" or "math")
     * @param definingClass of the module
     * @param lookup authorises access to the defining class.
     */
    public ModuleDef(String moduleName, Class<JM> definingClass,
            Lookup lookup) {
        this.name = moduleName;
        this.definingClass = definingClass;

        // Record the exposed methods as MethodDefs.
        List<MethodDef> methods = new LinkedList<>();
        for (Method m : definingClass.getDeclaredMethods()) {
            PythonMethod pm =
                    m.getDeclaredAnnotation(PythonMethod.class);
            if (pm != null) {
                addMethod(methods, moduleName, m, lookup);
            }
        }

        this.methods = methods.toArray(new MethodDef[methods.size()]);
    }

    /**
     * Add a {@link MethodDef} for the given method {@code m} to the
     * working list.
     */
    private static void addMethod(List<MethodDef> methods,
            String moduleName, Method m, Lookup lookup) {
        String name = m.getName();
        // Must be an instance method of the module type M
        assert Modifier.isStatic(m.getModifiers()) == false;

        try {
            // Signature of handle is to be (O,O[])O
            /*
             * In real life we would need run-time checks and
             * conversion, but this is just a toy.
             */
            MethodHandle mh = lookup.unreflect(m);
            MethodType mt = mh.type();
            int n = mt.parameterCount();
            assert n > 0;
            if (n == 1) {
                // Signature is currently (M)T
                // Add an ignored array argument (no args at run-time).
                mh = MethodHandles.dropArguments(mh, 1, OA);
            } else {
                // Signature is currently (M,A,B,...)T
                // We shall receive an array we spread to the args.
                mh = mh.asSpreader(OA, n - 1);
            }
            mh = mh.asType(MT.METHOD);
            MethodDef md = new MethodDef(name, mh);
            methods.add(md);
        } catch (ReflectiveOperationException e) {
            String msg = String.format(
                    "Cannot expose method %s.%s: due to %s", moduleName,
                    name, e);
            throw new PyException(msg, e);
        }
    }

    /**
     * Get the method definitions. This method is provided for test use
     * only. It isn't safe for public use.
     *
     * @return the method definitions
     */
    MethodDef[] getMethods() { return methods; }

    @Override
    public String getName() { return name; }

    @Override
    public void addMembers(JavaModule module) {
        PyDict d = module.getDict();
        for (MethodDef md : methods) {
            // Create function by binding to the module
            PyJavaFunction func = PyJavaFunction.forModule(md.name,
                    md.handle, module);
            d.put(md.name, func);
        }
    }

    /**
     * A {@code MethodDef} describes a built-in function or method as it
     * is declared in a Java module. It holds a handle for calling the
     * method.
     */
    // Compare CPython struct PyMethodDef
    static class MethodDef {

        final String name;
        final MethodHandle handle;

        MethodDef(String name, MethodHandle meth) {
            this.name = name;
            this.handle = meth;
        }

        @Override
        public String toString() {
            return String.format("%s[%s]", getClass().getSimpleName(),
                    name);
        }
    }
}
