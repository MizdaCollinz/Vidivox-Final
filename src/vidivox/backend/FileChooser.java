package vidivox.backend;

import java.awt.Container;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Creates a JFileChooser which is used in the browsing for Video and Audio
 * files throughout the program. Capable of storing the users most recent
 * directory for each time an instance is opened or closed.
 *
 */
public class FileChooser {

	private Container container;
	private String path;
	private File recentDirPath = new File("CustomVidivoxFolder/.recentPath");
	private String recentPath = "\\";
	private JFileChooser fileChooser;

	public FileChooser(Container container) {
		this.container = container;
		fileChooser = new JFileChooser();

		//
		if (!recentDirPath.exists()) {
			try {
				// Creates hidden file containing path of most recent directory
				// if not already created
				recentDirPath.createNewFile();
				// Sets alternative default directory
				fileChooser.setCurrentDirectory(new File("\\"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// Returns to the most recent directory if it has previously been
			// recorded
			findRecentPath(recentDirPath);
			fileChooser.setCurrentDirectory(new File(recentPath));
		}

		fileChooser.setDialogTitle("Choose File");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

	}

	// Updates the recent path in the hidden file
	private void updateRecentPath(String recentPath) {
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("CustomVidivoxFolder/.recentPath")));
			pw.write(path);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Reads the hidden file to find the most recent path
	private void findRecentPath(File recentDir) {
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(recentDir));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			recentPath = br.readLine();
			if (recentPath == null) {
				recentPath = "\\";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// Pass through the file filter to the current instance
	public void setFileFilter(FileNameExtensionFilter filter) {
		fileChooser.setFileFilter(filter);
	}

	// Most essential method to class, Opens the FileChooser dialog and returns
	// the path of the selected file
	public String getPath() {
		int result = fileChooser.showOpenDialog(container);
		if (result == JFileChooser.APPROVE_OPTION) {
			path = fileChooser.getSelectedFile().getAbsolutePath();
			updateRecentPath(path);
		} else if (result == JFileChooser.CANCEL_OPTION) {
			return null;
		}

		return path;
	}
}
