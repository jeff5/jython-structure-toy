package example.app;

import java.lang.invoke.MethodHandles;

import example.runtime.Exposed;
import example.runtime.PyType;
import example.runtime.PyUtil;
import example.runtime.PythonRuntime;

/**
 * A type used in the {@link Application} and defined with facilities in
 * the {@code runtime} package.
 */
class MyType {

    private int content;

    MyType(int content) { this.content = content; }

    @Exposed.PythonMethod
    Object __str__() { return "MyType(" + content + ")"; }

    @Exposed.PythonMethod
    static void set_content(MyType self, int v) {
        self.content = 2 * v;
    }

    @Override
    public String toString() { return PyUtil.defaultToString(this); }

    static final PyType TYPE = PythonRuntime.typeFactory.register("MyType",
            MyType.class, MethodHandles.lookup());
}
