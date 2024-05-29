package example.runtime;

import static example.internal.ClassShorthand.O;
import static example.internal.ClassShorthand.OA;
import static example.internal.ClassShorthand.V;

import java.lang.invoke.MethodType;

/** Commonly-used {@code MethodType}s. */
public interface MT {
    // Note: Interface fields are implicitly public static final
    /** {@code MethodType} of the handle to any exposed method. */
    MethodType METHOD = MethodType.methodType(O, O, OA);
    /** {@code MethodType} of the handle to a bound exposed method. */
    MethodType FUNCTION = MethodType.methodType(O, OA);
    /** {@code MethodType} of {@code __neg__} etc.. */
    MethodType UNARY = MethodType.methodType(O, O);
    /** {@code MethodType} of {@code __add__} etc.. */
    MethodType BINARY = MethodType.methodType(O, O, O);
    /** {@code MethodType} of {@code __call__}. */
    MethodType CALL = MethodType.methodType(O, O, OA);
    /** {@code MethodType} of a module constructor. */
    MethodType MODULE_CONS = MethodType.methodType(V);
}
