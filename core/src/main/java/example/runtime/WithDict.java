package example.runtime;

import java.util.Map;

/**
 * A Python object where objects have an explicit dictionary (that is
 * not necessarily a Python {@code dict} or directly
 * writable). A {@code type} object .
 */
public interface WithDict extends Crafted {
    /**
     * The instance dictionary. This is not necessarily a Python
     * {@code dict}, and may not be directly writable. Some implementing
     * types override the signature to specify the return is a
     * fully-fledged {@link PyDict}.
     *
     * @return instance dictionary
     */
    Map<Object, Object> getDict();
}
