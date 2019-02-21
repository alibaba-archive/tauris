package com.aliyun.tauris.metric;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ZhangLei on 16/12/2.
 */
public class LabelsKey implements Serializable {

    // This class could implement List, but that would confuse it's purpose

    /** Serialisation version */
    private static final long serialVersionUID = 4465448607415777705L;

    /** The individual keys */
    private final String[] keys;
    /** The cached hashCode */
    private transient int hashCode;

    /**
     * Constructor taking an array of keys which is cloned.
     * <p>
     * The keys should be immutable
     * If they are not then they must not be changed after adding to the LabelsKey.
     * <p>
     * This is equivalent to <code>new LabelsKey(keys, true)</code>.
     *
     * @param keys  the array of keys, not null
     * @throws IllegalArgumentException if the key array is null
     */
    LabelsKey(final String[] keys) {
        this(keys, true);
    }

    /**
     * Constructor taking an array of keys, optionally choosing whether to clone.
     * <p>
     * <b>If the array is not cloned, then it must not be modified.</b>
     * <p>
     * This method is public for performance reasons only, to avoid a clone.
     * The hashcode is calculated once here in this method.
     * Therefore, changing the array passed in would not change the hashcode but
     * would change the equals method, which is a bug.
     * <p>
     * This is the only fully safe usage of this constructor, as the object array
     * is never made available in a variable:
     * <pre>
     * new LabelsKey(new Object[] {...}, false);
     * </pre>
     * <p>
     * The keys should be immutable
     * If they are not then they must not be changed after adding to the LabelsKey.
     *
     * @param keys  the array of keys, not null
     * @param makeClone  true to clone the array, false to assign it
     * @throws IllegalArgumentException if the key array is null
     * @since 3.1
     */
    LabelsKey(final String[] keys, final boolean makeClone) {
        super();
        if (keys == null) {
            throw new IllegalArgumentException("The array of keys must not be null");
        }
        if (makeClone) {
            this.keys = keys.clone();
        } else {
            this.keys = keys;
        }

        calculateHashCode(keys);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets a clone of the array of keys.
     * <p>
     * The keys should be immutable
     * If they are not then they must not be changed.
     *
     * @return the individual keys
     */
    public List<String> getKeys() {
        return Arrays.asList(keys);
    }

    /**
     * Gets the key at the specified index.
     * <p>
     * The key should be immutable.
     * If it is not then it must not be changed.
     *
     * @param index  the index to retrieve
     * @return the key at the index
     * @throws IndexOutOfBoundsException if the index is invalid
     * @since 3.1
     */
    public String getKey(final int index) {
        return keys[index];
    }

    /**
     * Gets the size of the list of keys.
     *
     * @return the size of the list of keys
     * @since 3.1
     */
    public int size() {
        return keys.length;
    }

    //-----------------------------------------------------------------------
    /**
     * Compares this object to another.
     * <p>
     * To be equal, the other object must be a <code>LabelsKey</code> with the
     * same number of keys which are also equal.
     *
     * @param other  the other object to compare to
     * @return true if equal
     */
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof LabelsKey) {
            final LabelsKey otherMulti = (LabelsKey) other;
            return Arrays.equals(keys, otherMulti.keys);
        }
        return false;
    }

    /**
     * Gets the combined hash code that is computed from all the keys.
     * <p>
     * This value is computed once and then cached, so elements should not
     * change their hash codes once created (note that this is the same
     * constraint that would be used if the individual keys elements were
     * themselves {@link java.util.Map Map} keys.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * Gets a debugging string version of the key.
     *
     * @return a debugging string
     */
    @Override
    public String toString() {
        return "LabelsKey" + Arrays.toString(keys);
    }

    /**
     * Calculate the hash code of the instance using the provided keys.
     * @param keys the keys to calculate the hash code for
     */
    private void calculateHashCode(final Object[] keys)
    {
        int total = 0;
        for (final Object key : keys) {
            if (key != null) {
                total ^= key.hashCode();
            }
        }
        hashCode = total;
    }

    /**
     * Recalculate the hash code after deserialization. The hash code of some
     * keys might have change (hash codes based on the system hash code are
     * only stable for the same process).
     * @return the instance with recalculated hash code
     */
    private Object readResolve() {
        calculateHashCode(keys);
        return this;
    }
}
