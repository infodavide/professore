package org.infodavid.util.swing;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * The Class JTextFieldLimit.
 */
public class JTextFieldLimit extends JTextField { // NOSONAR Inheritance

    /**
     * The Class LimitDocument.
     */
    private class LimitDocument extends PlainDocument {

        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1276422359285756347L;

        /**
         * Insert string.
         * @param offset the offset
         * @param str the str
         * @param attr the attr
         * @throws BadLocationException the bad location exception
         */
        @Override
        public void insertString(final int offset, final String str, final AttributeSet attr) throws BadLocationException {
            if (str == null)
                return;

            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -766438953063304836L;

    /** The limit. */
    private final int limit;

    /**
     * Instantiates a new j text field limit.
     * @param limit the limit
     */
    public JTextFieldLimit(final int limit) {
        super();
        this.limit = limit;
    }

    /*
     * (non-javadoc)
     * @see javax.swing.JTextField#createDefaultModel()
     */
    @Override
    protected Document createDefaultModel() {
        return new LimitDocument();
    }
}