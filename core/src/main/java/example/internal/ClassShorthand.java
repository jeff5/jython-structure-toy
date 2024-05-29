package example.internal;

/**
 * Some shorthands used to construct method signatures,
 * {@code MethodType}s, etc..
 */
public interface ClassShorthand {
    // Note: Interface fields are implicitly public static final
    /** Shorthand for {@code Object.class}. */
    Class<Object> O = Object.class;
    /** Shorthand for {@code void.class}. */
    Class<?> V = void.class;
    /** Shorthand for {@code Object[].class}. */
    Class<Object[]> OA = Object[].class;
}
