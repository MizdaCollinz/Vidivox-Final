package vidivox.backend.eventlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import vidivox.backend.AudioConverter;
import vidivox.backend.AudioPlayer;
import vidivox.backend.RewindSession;
import vidivox.gui.MediaPlayerMain;

/**
 * Class dedicated to the functionality of the ControlPanel for the Primary
 * frame of the Vidivox Player (Control Panel being the Bottom row of buttons
 * for standard media player functionality) Generates all necessary Action
 * Listeners (and ChangeListeners)
 * 
 * @author Benjamin Collins BCOL602
 *
 */
public class ControlPanel {

	MediaPlayerMain frame;
	RewindSession rewinding;
	EmbeddedMediaPlayer video;
	AudioPlayer audioPlayerComponent;
	Boolean isPlaying;
	Boolean audioIsFinished;
	Boolean isMuted;

	public ControlPanel(MediaPlayerMain frame) {
		this.frame = frame;
		isMuted = false;
	}

	public void setVideo(EmbeddedMediaPlayer video) {
		this.video = video;
	}

	public ActionListener playbuttonAL() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Check if video chosen
				if (!frame.checkIfVideo(true)) {
					return;
				}

				isPlaying = frame.getIsPlaying();
				audioIsFinished = frame.getAudioIsFinished();
				audioPlayerComponent = frame.getAudioComponent();

				// Disable rewinds in progress
				if (rewinding != null) {
					rewinding.cancel(true);
					rewinding = null;
				}

				// Align video state with mute button, undo rewind/fastforward
				// muting
				if (isMuted) {
					video.mute(true);
				} else {
					video.mute(false);
				}

				// Resume from other state
				if (isPlaying == false) {

					video.setRate(1);
					video.play();
					frame.setIsPlaying(true);
					frame.setPlayButton(false);

					// Resume any audio being previewed currently that hasnt
					// already completed
					if ((!audioIsFinished) && (audioPlayerComponent != null)) {
						audioPlayerComponent.play();
					}

					// Pause if playing normally
				} else {
					if (video.isPlaying()) {
						video.pause();
					}
					frame.setIsPlaying(false);
					frame.setPlayButton(true);

					// Pause any audio being previewed when the video is paused
					if ((!audioIsFinished) && (audioPlayerComponent != null)) {
						audioPlayerComponent.pause();
					}

				}
			}
		};

	}

	public ActionListener rewindButtonAL() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Check if video chosen
				if (!frame.checkIfVideo(true)) {
					return;
				}

				isPlaying = frame.getIsPlaying();

				// Increase rewind if pressed again
				if (rewinding != null) {
					rewinding.increaseRate();
					return;
				}

				if (video.isPlaying()) {
					video.pause();
				}
				frame.setIsPlaying(false);
				frame.setPlayButton(true);

				// New BackgroundTask of Rewinding
				rewinding = new RewindSession(video);
				rewinding.execute();
			}

		};
	}

	public ActionListener forwardButtonAL() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Check if video chosen
				if (!frame.checkIfVideo(true)) {
					return;
				}
				// Ignore if rewinding
				if (rewinding != null) {
					return;
				}

				// Mute the video
				if (!video.isMute()) {
					video.mute();
				}

				float currentRate = video.getRate();
				float newRate = currentRate + 1;

				// Limit rate to 4x speed
				if (newRate > 4) {
					newRate = 4;
				}

				video.setRate(newRate);
				video.play();

				frame.setIsPlaying(false);
				frame.setPlayButton(true);

			}

		};
	}

	public ActionListener stopButtonAL() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Check if video chosen
				if (!frame.checkIfVideo(true)) {
					return;
				}

				video.stop();
				frame.setPlayButton(true);
				frame.setIsPlaying(false);

				// If Audio Player exists, allow stop button to reset it.
				audioPlayerComponent = frame.getAudioComponent();
				if (audioPlayerComponent != null) {
					audioPlayerComponent.stop();
				}
				frame.setAudioIsFinished(false);

			}

		};
	}

	public ActionListener outputButtonAL() {
		final AudioConverter ac = frame.getAC();

		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String newOutputFolder = (String) JOptionPane.showInputDialog(frame,
						"Set a new output folder for files", "[Output Folder]", JOptionPane.PLAIN_MESSAGE, null, null,
						ac.getOutputFolder());
				if (newOutputFolder != null) {
					// Assigns output folder to AudioConverter, ensures new
					// folder is initialised
					ac.setOutputFolder(newOutputFolder);
					ac.initialiseFolder();
				}
			}

		};
	}

	public ChangeListener soundCtrlCL() {
		return new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {

				video = frame.getVideo();
				JSlider soundCtrl = frame.getSoundCtrl();
				audioPlayerComponent = frame.getAudioComponent();

				// Change volume on media player and audio preview if present
				video.setVolume(soundCtrl.getValue() * 2);
				if (audioPlayerComponent != null) {
					audioPlayerComponent.setVolume(soundCtrl.getValue() * 2);
				}
			}
		};
	}

	public ActionListener muteButtonAL() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Toggles the muting of Video & Audio Media players
				if (frame.mute()) {
					isMuted = true;
				} else {
					isMuted = false;
				}

			}

		};
	}
}
