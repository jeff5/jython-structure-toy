package example.runtime;

public abstract class Representation {
    /**
     * The common type (class or interface) of Java classes representing
     * instances of the related Python {@code type}.
     */
    protected final Class<?> javaClass;

    /**
     * Create a {@code Representation} relating a (base) Java class to a
     * type. Creation of a {@code Representation} does not register the
     * association.
     *
     * @param javaClass the base of classes represented
     */
    protected Representation(Class<?> javaClass) {
        this.javaClass = javaClass;
    }

    Representation() {this(null);}

    /**
     * Get the Python type of the object <i>given that</i> this is the
     * representation object for it.
     *
     * @param x subject of the enquiry
     * @return {@code type(x)}
     */
    public abstract PyType pythonType(Object x);
    /**
     * A base Java class representing instances of the related Python
     * {@code type} associated with this {@code Representation}.
     *
     * @return base class of the representation
     */
    public Class<?> javaClass() { return javaClass; }

}
