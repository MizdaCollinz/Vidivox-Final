package vidivox.backend.eventlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import vidivox.backend.FileChooser;
import vidivox.gui.AddAudioFrame;
import vidivox.gui.MediaPlayerMain;
import vidivox.gui.SeparateFestivalFrame;
import vidivox.gui.StripAudioFrame;

/**
 * Class dedicated to the generation of ActionListeners for the SidePanel in the
 * Main JFrame
 * 
 * @author Benjamin Collins BCOL602
 */
public class SidePanel {

	private MediaPlayerMain frame;
	private String videoPath;
	private SeparateFestivalFrame persistentFestival;
	private String currentOwnAudio;
	private String currentVideo;

	public SidePanel(MediaPlayerMain frame) {
		this.frame = frame;
		persistentFestival = new SeparateFestivalFrame(frame);
	}

	public ActionListener selectVideoAL() {

		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileChooser fc = new FileChooser(frame);
				// Filter out files other than avi videos
				FileNameExtensionFilter filter = new FileNameExtensionFilter("AVI file", "avi");
				fc.setFileFilter(filter);

				// Retrieves a video file to open
				videoPath = fc.getPath();

				// Check if video is actually chosen
				if (videoPath == null || videoPath.length() == 0) {
					return;
				}

				frame.setOwnAudio(null);
				frame.playNewVideo(videoPath);

			}
		};
	}

	public ActionListener synthAudioAL() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Pauses all media, opens a new frame for generating festival
				// MP3s
				if (frame.getIsPlaying()) {
					frame.pauseAll();
				}
				persistentFestival.setLocationRelativeTo(frame);
				persistentFestival.setVisible(true);

			}
		};
	}

	public ActionListener stripAudioAL() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Check if video is selected, pause all media
				if (!frame.checkIfVideo(true)) {
					return;
				}
				if (frame.getIsPlaying()) {
					frame.pauseAll();
				}

				// Open up new StripAudio frame for further interaction
				StripAudioFrame saf = new StripAudioFrame(frame);
				// Update Output folder incase changed
				saf.setOutputFolder(frame.getAC().getOutputFolder());
				frame.setEnabled(false);
			}

		};
	}

	public ActionListener mergeAudioAL() {

		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!frame.checkIfVideo(true)) {
					return;
				} else if (currentOwnAudio == null) { // Merging cancelled due
														// to lack of audio
														// selection
					// Pause all media
					if (frame.getIsPlaying()) {
						frame.pauseAll();

					}
					// error pop up because no audio has been selected
					JOptionPane.showMessageDialog(frame, "Please select an audio file first", "Error",
							JOptionPane.ERROR_MESSAGE);

				} else { // Situation when merging is to occur
					// Pause all media
					if (frame.getIsPlaying()) {
						frame.pauseAll();
					}

					// Merges audio and moves to new video
					String mergedVideo = frame.getAC().embedAudio(currentVideo, currentOwnAudio);
					if (mergedVideo != null) {
						frame.setIsMerged(true);
						frame.playNewVideo(mergedVideo);
					}
				}
			}

		};
	}

	public ActionListener selectMP3AL() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Check if Video is already present, Pause all media if playing
				if (!frame.checkIfVideo(true)) {
					return;
				}
				if (frame.getIsPlaying()) {
					frame.pauseAll();
				}
				// Spawn new frame for further interaction with adding audio
				new AddAudioFrame(frame);
			}

		};
	}

	public void setOwnAudio(String currentOwnAudioIn) {
		currentOwnAudio = currentOwnAudioIn;
	}

	public void setCurrentVideo(String input) {
		currentVideo = input;
	}

	public String getCurrentVideo() {
		return currentVideo;
	}

	public String getVideoPath() {
		return videoPath;
	}

	public String getOwnAudio() {
		return currentOwnAudio;
	}
}
