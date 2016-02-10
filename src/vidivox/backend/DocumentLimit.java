package vidivox.backend;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * DocumentFilter used to limit the number of characters entered into a textbox
 * 
 * @author Benjamin Collins BCOL602
 *
 */
public class DocumentLimit extends DocumentFilter {

	int maximumCharacters;

	public DocumentLimit(int maximum) {
		maximumCharacters = maximum;
	}

	// If new string will cause the limit to be breached it is not inserted.
	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		// Below the character limit, insert strings or text as usual
		if ((fb.getDocument().getLength() + str.length()) <= maximumCharacters)
			super.insertString(fb, offs, str, a);

	}

	// Same as insertString method
	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
		if ((fb.getDocument().getLength() + str.length() - length) <= maximumCharacters) {
			super.replace(fb, offs, length, str, a);
		}
	}
}
