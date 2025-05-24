package example.runtime;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import example.internal.ImplUtil;

/**
 * A {@code PyJavaFunction} represents the Python type
 * {@code builtin_function_or_method} instances of which {@code repr()}
 * as {@code <built-in function ...>}. It is a callable object, since it
 * defines the instance method {@code __call__}, taking as parameter the
 * array of arguments supplied from the call site.
 */
public class PyJavaFunction implements WithType {

    /** Name of the function. */
    final String name;

    /**
     * The object to which this is bound as target. This field should
     * contain the bound target ({@code object} or {@code type}), which
     * should also have been bound into the {@link #handle} supplied in
     * construction. A function obtained from a module may be a method
     * bound to an instance of that module.
     */
    final Object self;

    /**
     * A Java {@code MethodHandle} that implements the function or bound
     * method. The type of this handle is always {@code (O,OA)} because
     * it has been "prepared" by binding the first argument of the
     * standard call signature {@code (O,O,OA)}.
     */
    final MethodHandle handle;

    /**
     * Construct a Python {@code builtin_function_or_method} object,
     * optionally bound to a particular "self" object, specifying the
     * prepared method handle.
     *
     * @param name of the method
     * @param handle a prepared prepared to the method defined
     * @param self object to which bound (or {@code null} if a static
     *     method)
     */
    protected PyJavaFunction(String name, MethodHandle handle,
            Object self) {
        this.name = name;
        this.handle = handle;
        this.self = self;
    }

    /**
     * Construct a {@code PyJavaFunction} from an {@link ArgParser} and
     * {@code MethodHandle} for the implementation method. The arguments
     * described by the parser do not include "self". This is the
     * factory we use to create a function in a module.
     *
     * @param name of method
     * @param method raw handle to the method defined
     * @param self object to which bound (the module)
     * @return A method bound to {@code self}
     */
    // Compare CPython PyCFunction_NewEx in methodobject.c
    static PyJavaFunction forModule(String name, MethodHandle method,
            JavaModule module) {
        // Bind a provided (O,O[])O handle as (O[])O.
        MethodHandle mh = method.bindTo(module).asType(MT.FUNCTION);
        return new PyJavaFunction(name, mh, module);
    }

    /**
     * Construct a {@code PyJavaFunction} from a {@link PyMethodDescr}
     * and an object to bind. The {@link PyMethodDescr} provides the
     * parser and unbound prepared {@code MethodHandle}. The arguments
     * described by the parser do not include "self". This is the
     * factory that supports descriptor {@code __get__}.
     *
     * @param descr descriptor being bound
     * @param self object to which bound.
     * @return a Java method object supporting the signature
     * @throws TypeError if {@code self} is not compatible with
     *     {@code descr}
     * @throws Throwable on other errors while chasing the MRO
     */
    // Compare CPython PyCFunction_NewEx in methodobject.c
    static PyJavaFunction from(PyMethodDescr descr, Object self)
            throws Throwable {
        MethodHandle handle = descr.getHandle().bindTo(self);
        return new PyJavaFunction(descr.name, handle, self);
    }

    /**
     * Return the bound handle contained in this function.
     *
     * @return corresponding handle
     */
    public MethodHandle getHandle() throws Throwable {
        return handle;
    }

    // slot functions -------------------------------------------------

    @Exposed.PythonMethod
    static Object __str__(PyJavaFunction f) {
        Object self = f.self;
        if (self == null || self instanceof PyModule)
            return String.format("<built-in function '%s'>", f.name);
        else
            return String.format("<built-in method '%s' of %s>", f.name,
                    ImplUtil.toAt(self));
    }

    /**
     * Call this object with the given arguments.
     *
     * It is important to recognise that the target of the
     * {@code __call__} method is this {@code function} object, while
     * from the perspective of executing code, the "logical target" is
     * an object to which a method in a type has been bound, or (less
     * obviously) the {@code module} that defined the {@code function}
     * object. In either case, the target is identified by this
     * function's {@code __self__}.
     *
     * @param args arguments of the call
     * @return result of call
     * @throws Throwable
     */
    @Exposed.PythonMethod
    static Object __call__(PyJavaFunction f, Object[] args)
            throws Throwable {
        /*
         * The object that is the target is already bound into the
         * handle. It is either the target object of a method call, or
         * the module instance that defined the function. We therefore
         * only have to supply the arguments.
         */
        return f.handle.invoke(args);
    }

    @Override
    public PyType getType() { return TYPE; }

    @Override
    public String toString() { return PyUtil.defaultToString(this); }

    public static final PyType TYPE = PythonRuntime.typeFactory
            .register("builtin_function_or_method",
                    MethodHandles.lookup());
}
