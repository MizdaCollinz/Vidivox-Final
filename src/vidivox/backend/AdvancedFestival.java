package vidivox.backend;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Creates scheme files to add complexity to the text to speech generation
 * 
 * @author Benjamin Collins BCOL602
 *
 */
public class AdvancedFestival {
	// Default Files
	String outputScheme = "CustomVidivoxFolder/.scheme.scm";
	String previewScheme = "CustomVidivoxFolder/.pscheme.scm";
	// Default Scheme Information
	String slow = "(Parameter.set 'Duration_Stretch 1.5)";
	String normal = "(Parameter.set 'Duration_Stretch 1.0)";
	String fast = "(Parameter.set 'Duration_Stretch 0.75)";

	PrintWriter writer, writer2;
	String currentSpeed;

	public AdvancedFestival() {
		currentSpeed = "Normal";
	}

	// Initialises the scheme files with a standard speaking speed
	public void createSchemeFile() {
		try {
			writer = new PrintWriter(outputScheme, "UTF-8");
			writer2 = new PrintWriter(previewScheme, "UTF-8");

			writer.write(normal);
			writer2.write(normal);

			writer.close();
			writer2.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	// Update scheme files by overwriting them with the new speed
	public void updateSchemeFile(String speed) {
		try {
			currentSpeed = speed;
			writer = new PrintWriter(outputScheme, "UTF-8");
			writer2 = new PrintWriter(previewScheme, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (speed.equals("Fast")) {
			writer.write(fast);
			writer2.write(fast);
		} else if (speed.equals("Normal")) {
			writer.write(normal);
			writer2.write(normal);
		} else if (speed.equals("Slow")) {
			writer.write(slow);
			writer2.write(slow);
		}
		writer.close();
		writer2.close();
	}

	// Overwrite any previous scheme content with the current speed and append
	// the message to be spoken
	// Avoids any stacking of "SayText" commands
	public void updatePreviewScheme(String text) {

		try {
			updateSchemeFile(currentSpeed);
			writer2 = new PrintWriter(new FileOutputStream(new File(previewScheme), true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		writer2.append("(SayText \"" + text + "\")");
		writer2.close();
	}
}
