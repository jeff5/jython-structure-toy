package example.extension;

import java.lang.invoke.MethodHandles;

import example.runtime.Exposed;
import example.runtime.JavaModule;

public class Extension extends JavaModule {

    @Exposed.PythonMethod
    public int foo(int x) {
        return ((x - 12)*x + 47)*x - 18;
    }

    public Extension() {
        super(DEFINITION);
    }

    private static final JavaModule.Definition DEFINITION =
            JavaModule.define("my_extension", MethodHandles.lookup());
}
