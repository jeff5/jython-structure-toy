// Copyright (c)2023 Jython Developers.
// Licensed to PSF under a contributor agreement.
package example.core;

import java.lang.invoke.MethodHandle;

import example.internal.PyJavaFunction;
import example.internal.PyMethodDescr;
import example.runtime.PyDict;
import example.runtime.PyType;

/** Common run-time constants and constructors. */
public class Py {

    /**
     * Return empty Python {@code dict}.
     *
     * @return {@code dict()}
     */
    public static PyDict dict() { return new PyDict(); }


}
