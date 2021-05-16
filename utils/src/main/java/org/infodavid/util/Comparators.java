package org.infodavid.util;

import java.util.Comparator;

/**
 * The Class Comparators.
 */
public class Comparators {

    /**
     * The Class StringLengthComparator.
     */
    public static class StringLengthComparator implements Comparator<String> {
        /*
         * (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(final String o1, final String o2) {
            if (o1.length() != o2.length()) {
                return o1.length() - o2.length();
            }

            return o1.compareTo(o2);
        }
    }

    /**
     * Instantiates a new comparators.
     */
    private Comparators() {
        super();
    }
}
