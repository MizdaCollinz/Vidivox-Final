package vidivox.backend;

import uk.co.caprica.vlcj.component.AudioMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayer;

/**
 * Audio Media Player class used to preview MP3 files
 * 
 * @author Benjamin Collins BCOL602
 *
 */
public class AudioPlayer {

	private final AudioMediaPlayerComponent mediaPlayerComponent;
	private MediaPlayer audio;

	public AudioPlayer(final vidivox.gui.MediaPlayerMain frame) {
		mediaPlayerComponent = new AudioMediaPlayerComponent();
		audio = mediaPlayerComponent.getMediaPlayer();

		audio.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(MediaPlayer mediaPlayer) {
				// sets boolean to true when the audio has finished playing
				frame.setAudioIsFinished(true);
			}

		});
	}

	// Plays the selected MP3
	public void start(String mrl) {
		audio.playMedia(mrl);
	}

	public void pause() {
		// pauses audio if it is not paused
		if (audio.isPlaying()) {
			audio.pause();
		}
	}

	// Adapts following method to pass through call directly to MediaPlayer

	public void play() {
		audio.play();
	}

	public void setVolume(int volume) {
		audio.setVolume(volume);
	}

	public void stop() {
		audio.stop();
	}

}
