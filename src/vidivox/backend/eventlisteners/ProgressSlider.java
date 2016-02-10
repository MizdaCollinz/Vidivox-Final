package vidivox.backend.eventlisteners;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import vidivox.gui.MediaPlayerMain;

@SuppressWarnings("serial")
/**
 * Modified JSlider for the purpose of displaying video progress Allows users to
 * drag the slider to a different position in the video Clicking of the slider
 * moves toward the cursor by several major ticks
 * 
 * @author Benjamin Collins BCOL602
 *
 */
public class ProgressSlider extends JSlider {
	private EmbeddedMediaPlayer video;
	private long length;

	private boolean playing;

	public ProgressSlider(final MediaPlayerMain frame) {
		super();

		this.setValue(0);
		this.setMaximum(10000);
		this.setMajorTickSpacing(10);

		final ProgressSlider thisSlider = this;
		ChangeListener l = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// checks boolean so statechanged is only 'activated' when we
				// move the slider to new position

				// Avoid statechanged while updating slider position
				if (playing) {
					return;
				}
				// Set to 0 if no video selected
				if (!frame.checkIfVideo(false)) {
					thisSlider.setValue(0);
					return;
				}

				float position = thisSlider.getValue();
				// If End of video is selected, move to just before the end of
				// the video to avoid errors
				if (position == 10000f) {
					video.setPosition(0.999f);
				} else {
					video.setPosition(position / 10000);
				}
			}
		};

		this.addChangeListener(l);
	}

	public void setVideo(EmbeddedMediaPlayer video) {
		this.video = video;
	}

	public void setLength() {
		this.length = video.getLength();
	}

	// Calculate percentage of total video complete, assign position as a
	// fraction of 10000 to slider
	public void updateSlider(long currentTime) {

		if (length != 0) {
			long sliderPosition = (currentTime * 10000 / length);
			playing = true;
			this.setValue((int) sliderPosition);
			playing = false;
		}
	}
}
