package vidivox.backend;

import javax.swing.SwingWorker;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * SwingWorker class used for rewinding Loops skipping backwards until the
 * process is cancelled
 * 
 * @author benjamin
 *
 */
public class RewindSession extends SwingWorker<Object, Object> {
	private EmbeddedMediaPlayer video;
	private int rate = -50; // Moves backwards 50 milliseconds at a time, every
							// cycle

	public RewindSession(EmbeddedMediaPlayer video) {
		this.video = video;

	}

	@Override
	protected Object doInBackground() throws Exception {
		while (!isCancelled()) {
			// Skip backwards 40x per second, backwards 0.05 seconds of video
			Thread.sleep(25);
			// Do nothing if at start of video
			if (video.getPosition() == 0f) {
				continue;
			} else {
				video.skip(rate);
			}
		}
		return null;
	}

	// Increases rate to maximum of -4x
	public void increaseRate() {
		rate = rate - 25;

		if (rate < -100) {
			rate = -100;
		}
	}

}
