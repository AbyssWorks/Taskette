package com.abyssworks.timetracker.util;

/**
 * The following data structure defines a Pair object which
 * can be used to store a couple.
 *
 * @param <F> The type of the first item.
 * @param <S> The type of the second item.
 *
 * @author Dysterio
 */
public class Pair<F, S> {
    private F firstItem;
    private S secondItem;

    /**
     * Initialize a new pair.
     *
     * @param firstItem The first item in the pair.
     * @param secondItem The second item in the pair.
     */
    public Pair(F firstItem, S secondItem) {
        this.firstItem = firstItem;
        this.secondItem = secondItem;
    }

    /** Returns the first item in the pair. */
    public F getFirstItem() {
        return this.firstItem;
    }
    /** Updates the first item in the pair. */
    public void setFirstItem(F firstItem) {
        this.firstItem = firstItem;
    }

    /** Returns the second item in the pair. */
    public S getSecondItem() {
        return this.secondItem;
    }
    /** Updates the second item in the pair. */
    public void setSecondItem(S secondItem) {
        this.secondItem = secondItem;
    }
}
