package vidivox.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import vidivox.gui.MediaPlayerMain;

/**
 * This class is primarily dedicated to a majority of the bash functionality
 * Covers synthesis of Festival Audio Covers stripping of audio from video
 * files. Covers merging of Audio into the Video file
 * 
 * @author Benjamin Collins BCOL602
 *
 */
public class AudioConverter {

	private String fileName;
	private JFrame relativeFrame;
	private JFrame festivalFrame;
	private MediaPlayerMain mediaPlayer;
	private String outputFolder = "CustomVidivoxFolder";

	public AudioConverter(JFrame frame) {
		relativeFrame = frame;
		initialiseFolder();
		outputFolder = "CustomVidivoxFolder";
	}

	// Sets up folder for MP3 and Video outputs
	public void initialiseFolder() {

		String outputPath = outputFolder.replace(" ", "\\ ");
		String cmd = "mkdir -p " + outputPath;
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Used to read out input text using festival
	public Process convertToAudio(String text) {

		mediaPlayer.updatePreview(text);
		try {
			// Momentarily stop to allow variables to update before proceeding
			// Allows scheme files to be prepared
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		String cmd = "festival -b \"CustomVidivoxFolder/.pscheme.scm\"";

		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			return builder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Converts festival input to a wav file
	public Boolean convertToWav(String text) {

		File f, g;

		// Retrieves filename from user to save wav file, stored in instance
		while (true) {
			fileName = JOptionPane.showInputDialog(festivalFrame,
					"Please name the synthesized speech file [Extension is not required]");
			String cleanFileName = fileName;
			fileName = outputFolder + "/" + fileName;

			f = new File(fileName + ".mp3");
			g = new File(fileName + ".wav");

			// Cancel button allows user to back out from saving an mp3
			if (cleanFileName == null) {
				return false;
				// Empty input trigger
			} else if (cleanFileName.trim().length() == 0) {
				JOptionPane.showMessageDialog(festivalFrame, "The name field was left blank.");
				// File already existing triggers here
			} else if (f.exists() || g.exists()) {
				JOptionPane.showMessageDialog(festivalFrame, "This file already exists.");
				// No errors, proceed
			} else {
				break;
			}

		}
		// Conver to bash-friendly file path
		fileName = fileName.replace(" ", "\\ ");
		String cleanFileName = fileName.replace("\\ ", " ");

		String cmd = "echo " + text + "| text2wave -o " + fileName + ".wav"
				+ " -eval \"CustomVidivoxFolder/.scheme.scm\"";

		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();

			// Checks whether process exits successfully, returns feedback to
			// user
			if (process.exitValue() != 0) {
				JOptionPane.showMessageDialog(festivalFrame, "Failed to create MP3.", "Error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(festivalFrame, cleanFileName + ".mp3 successfully generated.", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	// Converts from wav file to mp3 file using FFMPEG
	public void wavToMp3() {

		String cmd = "ffmpeg -i " + fileName + ".wav -f mp3 " + fileName + ".mp3";

		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();

			// Remove old wav file after mp3 is generated
			cmd = "rm " + fileName + ".wav";
			builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			builder.start();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	// Produces an mp3 file at a delayed position to correspond with where the
	// user selected the audio to appear in the video
	public String delayedPreview(String delayAudio, long delayTime) {
		try {

			String fileName = outputFolder + "/" + "previewMP3.mp3";

			// Ensure any old preview file is removed and therefore will be
			// overwritten
			File f = new File(fileName);
			if (f.exists()) {
				f.delete();
			}
			String cmd;

			// Bash Friendly Path Name
			delayAudio = delayAudio.replace(" ", "\\ ");
			String outputFile = fileName.replace(" ", "\\ ");

			cmd = "ffmpeg -i " + delayAudio + " -filter_complex \"[0:a]adelay=" + delayTime
					+ "[audio]\" -map \"[audio]\"" + " " + outputFile;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			// Provides feedback on success of process
			if (process.exitValue() == 0) {
				JOptionPane.showMessageDialog(relativeFrame, "Audio will now preview at specified position.", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(relativeFrame, "Audio failed to generate at specified time. Try again",
						"Success", JOptionPane.ERROR_MESSAGE);
			}

			// Report back new mp3 file to be played for preview
			return fileName;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Strips audio from the current video, output to custom file name
	public void removeAudio(String currentVideo, String outputVideo) {

		try {
			currentVideo = currentVideo.replace(" ", "\\ ");
			outputVideo = outputVideo.trim().replace(" ", "\\ ");

			outputVideo = outputFolder + "/" + outputVideo;

			String cmd = "ffmpeg -i " + currentVideo + " -c copy -an " + outputVideo;

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			// Report back on success of process
			if (process.exitValue() == 0) {
				JOptionPane.showMessageDialog(festivalFrame, "Audio successfully removed.", "Success",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(festivalFrame, "Audio stripping failed.", "Success",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Merges the selected audio into the selected video file
	public String embedAudio(String currentVideo, String currentOwnAudio) {
		try {
			String currentAudio = null;

			currentVideo = currentVideo.replace(" ", "\\ ");

			File f;
			String outputFile;

			// Loops a request for a filename until a valid one is input
			while (true) {
				outputFile = JOptionPane.showInputDialog(relativeFrame,
						"Select a name for the outputted video file [File extension is automated]");
				String outputVideo = outputFile + ".avi";
				f = new File(outputFolder + "/" + outputVideo);

				if (f.exists() && outputFile.trim().length() != 0) {
					JOptionPane.showMessageDialog(relativeFrame, "This file already exists.", "Error",
							JOptionPane.ERROR_MESSAGE);
					// Cancels out of the saving if the cancel button is
					// selected
				} else if (outputFile == null) {
					return null;
					// Error if blank output
				} else if (outputFile.trim().length() == 0) {
					JOptionPane.showMessageDialog(relativeFrame, "The name field was left blank.", "Error",
							JOptionPane.ERROR_MESSAGE);
					// Proceed if no other issues
				} else {
					outputFile = outputVideo;
					break;
				}

			}

			outputFile = outputFolder + "/" + outputFile;
			outputFile = outputFile.replace(" ", "\\ ");

			// Converts to bash-friendly path names, changes spaces to "\ "
			currentOwnAudio = currentOwnAudio.replace(" ", "\\ ");
			currentAudio = currentOwnAudio;

			String cmd;

			if (isAudioless(currentVideo) == null || isAudioless(currentVideo).equals("")) {
				// Merges audio stream into audioless video
				cmd = "ffmpeg -i " + currentVideo + " -i " + currentAudio + " -map 0:v -map 1:a -c copy " + outputFile;

			} else {

				// Merges audio stream into the video
				cmd = "ffmpeg -i " + currentVideo + " -i " + currentAudio
						+ " -filter_complex \"[1:a]apad[audio] ; [audio][0:a]amerge[aout]\" -map 0:v -map \"[aout]\" -c:v copy "
						+ outputFile;

			}

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			// Checks exit status to ensure merging is successful
			if (process.exitValue() != 0) {
				JOptionPane.showMessageDialog(relativeFrame,
						"Merge failed, remember to add the correct video extension to the output name.");
				return null;
			} else {
				JOptionPane.showMessageDialog(relativeFrame,
						"Merge successful, The merged video will now be automatically selected.");
			}
			outputFile = outputFile.replace("\\ ", " ");
			return outputFile;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Probes selected video file to see if an audio track exists
	public String isAudioless(String currentVideo) {
		try {
			String cmd = "ffprobe -i " + currentVideo + " -show_streams -select_streams a -loglevel error>info.txt";

			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();

			// Write results to txt file, read results and delete temporary txt
			// file
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("info.txt")));
			String output = br.readLine();
			File f = new File("info.txt");
			f.delete();
			br.close();

			return output;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;

	}

	public String getFileName() {
		return fileName;
	}

	public void setOutputFolder(String outputFolderName) {
		outputFolderName.replace(" ", "\\ ");
		outputFolder = outputFolderName;
	}

	public String getOutputFolder() {
		return outputFolder;
	}

	public void setFestivalFrame(JFrame frame) {
		festivalFrame = frame;
	}

	public void setMediaPlayer(MediaPlayerMain mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}

}
