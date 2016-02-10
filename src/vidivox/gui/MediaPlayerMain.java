package vidivox.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeListener;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import vidivox.backend.AdvancedFestival;
import vidivox.backend.AudioConverter;
import vidivox.backend.AudioPlayer;
import vidivox.backend.eventlisteners.ControlPanel;
import vidivox.backend.eventlisteners.MenuBar;
import vidivox.backend.eventlisteners.ProgressSlider;
import vidivox.backend.eventlisteners.SidePanel;

@SuppressWarnings("serial")

/**
 * THE MAIN CLASS WHICH GENERATES THE PRIMARY FRAME FOR THE PROGRAM
 * 
 * @author Benjamin Collins BCOL602
 *
 */
public class MediaPlayerMain extends JFrame {

	// DEFAULTS
	final private ImageIcon PLAYICON = new ImageIcon("imgs/play.png");
	final private ImageIcon PAUSEICON = new ImageIcon("imgs/pause.png");
	final private ImageIcon MUTEICON = new ImageIcon("imgs/mute.png");
	final private ImageIcon VOLUMEICON = new ImageIcon("imgs/volume.png");
	final private ImageIcon REWINDICON = new ImageIcon("imgs/rewind.png");

	// Primary Media Components
	private EmbeddedMediaPlayer video;
	private AudioPlayer audioPlayerComponent;

	// Primary Instances of other classes and current class
	private MediaPlayerMain frame;
	private ControlPanel cp;
	private SidePanel sp;
	private AdvancedFestival af;
	private AudioConverter ac;

	// References to main panels in the main JFrame
	private JPanel mainPanel;
	private JPanel controlPanel;
	private JPanel rightpanel;

	// References to important UI features which change state, or are virtually
	// activated elsewhere
	private JSlider soundCtrl;
	private ProgressSlider progress;
	private JLabel progressLabel;
	private JButton playButton;
	private JButton stopbutton;
	private JButton button3; // SelectVideo Button
	private JButton mute;

	// Boolean members
	private boolean isPlaying = false;
	private boolean isVideo = false;
	private boolean audioIsFinished = true;
	private boolean isMerged = false;

	// Field for retaining the default font across all newly created fonts
	private static String defaultFont;

	MediaPlayerMain() {
		setTitle("Vidivox Release 1.0");
		setSize(1024, 560);
		setLocation(200, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getRootPane().setBorder(BorderFactory.createMatteBorder(0, 3, 0, 3, Color.WHITE));
		new MenuBar(this);

		// Initialising of many other class instances
		frame = this;
		ac = new AudioConverter(this);
		ac.setMediaPlayer(this);
		cp = new ControlPanel(this);
		sp = new SidePanel(this);
		af = new AdvancedFestival();
		af.createSchemeFile();
		progress = new ProgressSlider(this);

		playButton = new JButton();

		// Timer to update progress bar and progress label
		int delay = 100;
		ActionListener updateSlider = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (frame.checkIfVideo(false)) {
					progress.setLength();
					progress.updateSlider(video.getTime());

					String labelTime = getCurrentTime() + " / " + getVideoTime();
					progressLabel.setText(labelTime);
				}
			}
		};

		Timer timer = new Timer(delay, updateSlider);
		timer.start();

	}

	public static void main(final String args[]) {

		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),
				"/Applications/vlc-2.0.0/VLC.app/Contents/MacOS/lib");
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				try {
					// Sets the Look And Feel to the Nimbus style which is
					// compatible with Java 1.6 and above
					for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
						if ("Nimbus".equals(info.getName())) {
							UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}

				Font defaultFONT = new JLabel().getFont();
				defaultFont = defaultFONT.getName();

				// Pieces together the entire Main JFrame using various methods
				MediaPlayerMain mainFrame = new MediaPlayerMain();
				mainFrame.setVisible(true);
				mainFrame.attachMainPanel();
				mainFrame.constructControlPanel();
				mainFrame.attachControls();
				mainFrame.attachSidePanel();
				mainFrame.attachMediaPanel();

			}
		});

	}

	// Sets up the Main Panel as the content pane for the main frame which holds
	// all other panels
	public void attachMainPanel() {

		// MAIN PANEL
		mainPanel = new JPanel();
		BorderLayout mainlayout = new BorderLayout(3, 0);
		mainPanel.setLayout(mainlayout);
		frame.setContentPane(mainPanel);
	}

	// Sets up the player control panel
	// ActionListeners are created in the ControlPanel class
	public void constructControlPanel() {

		// Play Button
		playButton.setPreferredSize(new Dimension(100, 35));
		playButton.setFont(new Font(defaultFont, Font.BOLD, 12));
		playButton.addActionListener(cp.playbuttonAL());
		playButton.setIcon(PLAYICON);

		// Rewind Button
		JButton rewindbutton = new JButton();
		rewindbutton.setIcon(REWINDICON);
		rewindbutton.setFont(new Font(defaultFont, Font.BOLD, 12));
		rewindbutton.addActionListener(cp.rewindButtonAL());

		// FastForward Button
		JButton forwardbutton = new JButton();
		ImageIcon forwardIcon = new ImageIcon("imgs/forward.png");
		forwardbutton.setIcon(forwardIcon);

		forwardbutton.setFont(new Font(defaultFont, Font.BOLD, 12));
		forwardbutton.addActionListener(cp.forwardButtonAL());

		// Stop Button
		stopbutton = new JButton();
		ImageIcon stopIcon = new ImageIcon("imgs/stop.png");

		stopbutton.setIcon(stopIcon);
		stopbutton.setFont(new Font(defaultFont, Font.BOLD, 12));
		stopbutton.setPreferredSize(new Dimension(100, 35));
		stopbutton.addActionListener(cp.stopButtonAL());

		// Output Folder Button
		JButton outputButton = new JButton("Output Folder");
		outputButton.setFont(new Font(defaultFont, Font.BOLD, 13));
		outputButton.setPreferredSize(new Dimension(150, 35));
		outputButton.addActionListener(cp.outputButtonAL());

		controlPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		controlPanel.add(rewindbutton);
		controlPanel.add(playButton);
		controlPanel.add(stopbutton);
		controlPanel.add(forwardbutton);
		controlPanel.add(outputButton);

	}

	// Attach the control panel and the volume controls to the main JFrame
	public void attachControls() {

		JLabel volumeLabel = new JLabel("Volume:");
		volumeLabel.setFont(new Font(defaultFont, Font.BOLD, 12));
		// Slider used for volume control
		soundCtrl = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
		soundCtrl.setMajorTickSpacing(25);
		soundCtrl.setPaintTicks(true);

		ChangeListener l = cp.soundCtrlCL();
		soundCtrl.addChangeListener(l);

		// Mute Button
		mute = new JButton();
		mute.setIcon(VOLUMEICON);
		mute.addActionListener(cp.muteButtonAL());

		JPanel southPanel = new JPanel(new BorderLayout());
		JPanel soundPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		soundPanel.add(soundCtrl);
		soundPanel.add(mute, FlowLayout.LEFT);
		soundPanel.add(volumeLabel, FlowLayout.LEFT);

		southPanel.add(controlPanel, BorderLayout.WEST);
		southPanel.add(soundPanel, BorderLayout.EAST);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
	}

	// Attach the Side Panel of editing controls to the main JFrame
	// All action listeners are generated in the SidePanel class
	public void attachSidePanel() {

		// Opens a file chooser for the user to browse for a video to watch/edit
		button3 = new JButton("Select Video");
		button3.setFont(new Font(defaultFont, Font.BOLD, 12));
		button3.addActionListener(sp.selectVideoAL());

		// Allows the user to synthesize an MP3 file using festival text to
		// speech
		JButton button4 = new JButton("Synthesize Audio");
		button4.setFont(new Font(defaultFont, Font.BOLD, 12));
		button4.addActionListener(sp.synthAudioAL());

		// Allows all audio to be stripped from the video, or removes the
		// currently selected preview audio
		JButton button5 = new JButton("Strip Audio");
		button5.setFont(new Font(defaultFont, Font.BOLD, 12));
		button5.addActionListener(sp.stripAudioAL());

		// Embeds selected audio tracks into the video file
		JButton button6 = new JButton("Merge Selected Audio");
		button6.setFont(new Font(defaultFont, Font.BOLD, 12));
		button6.addActionListener(sp.mergeAudioAL());

		// Selects audio which is previewed by default and is prepped to be
		// merged/embedded within the video
		JButton button7 = new JButton("Select MP3 File");
		button7.setFont(new Font(defaultFont, Font.BOLD, 12));
		button7.addActionListener(sp.selectMP3AL());

		rightpanel = new JPanel(new GridLayout(5, 1, 0, 3));
		rightpanel.add(button3);
		rightpanel.add(button4);
		rightpanel.add(button7);
		rightpanel.add(button5);
		rightpanel.add(button6);

		mainPanel.add(rightpanel, BorderLayout.EAST);
	}

	// Generates the Central Media Component which displays videos
	public void attachMediaPanel() {
		// MEDIA COMPONENT
		JPanel mediaPanel = new JPanel(new BorderLayout());

		EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		video = mediaPlayerComponent.getMediaPlayer();
		cp.setVideo(video);
		// Ensure unmuted when opened
		if (video.isMute()) {
			video.mute();
		}
		video.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(MediaPlayer mediaPlayer) {
				// sets boolean to true when the audio has finished playing
				stopbutton.doClick();
			}

		});

		progressLabel = new JLabel("00:00" + " / " + "00:00");

		JPanel progressPanel = new JPanel(new BorderLayout(3, 0));
		progressPanel.setBorder(new EmptyBorder(0, 0, 0, 2));
		progressPanel.add(progress, BorderLayout.CENTER);
		progressPanel.add(progressLabel, BorderLayout.EAST);

		mediaPanel.add(mediaPlayerComponent, BorderLayout.CENTER);
		mediaPanel.add(progressPanel, BorderLayout.SOUTH);
		mainPanel.add(mediaPanel, BorderLayout.CENTER);
	}

	// Toggle the mute status including the corresponding icon
	public boolean mute() {
		if (video.isMute()) {
			video.mute(false);
			mute.setIcon(VOLUMEICON);
			return false;
		} else {
			video.mute(true);
			mute.setIcon(MUTEICON);
			return true;
		}
	}

	// Checks if a video has been chosen to play by the user
	public boolean checkIfVideo(boolean display) {
		if (!isVideo) {
			if (display) {
				JOptionPane.showMessageDialog(frame, "Please select a video first.", "Step One: Select a video",
						JOptionPane.ERROR_MESSAGE);
			}
			return false;
		} else {
			return true;
		}

	}

	// Called when a new video is selected to play and be the "Current Video"
	public void playNewVideo(String newvideo) {
		sp.setCurrentVideo(newvideo);

		// Set Progress bar
		progress.setVideo(video);
		progress.setValue(0);

		// Autoplay video
		video.playMedia(newvideo);
		video.mute(false);
		mute.setIcon(VOLUMEICON);

		frame.setTitle("Vidivox Release 1.0: " + newvideo);
		isPlaying = true;
		isVideo = true;
		setPlayButton(false);
		progress.setLength();
	}

	// Retrieves a mm:ss string version of the current position of the video
	public String getCurrentTime() {
		float millis2 = frame.getVideo().getPosition() * frame.getVideo().getLength();
		long millis = (long) millis2;
		String currentTime = getMMSS(millis);

		return currentTime;
	}

	// Returns the duration of the video in mm:ss format
	public String getVideoTime() {
		long millis = frame.getVideo().getLength();
		String currentTime = getMMSS(millis);
		return currentTime;
	}

	// Returns a mm:ss time format from a total in milliseconds
	public String getMMSS(long millis) {
		String currentTime = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		return currentTime;
	}

	// Pause the video and any playing audio-players for previews
	public void pauseAll() {
		if (video.isPlaying()) {
			video.pause();
		}
		isPlaying = false;
		setPlayButton(true);
		if (!audioIsFinished & audioPlayerComponent != null) {
			audioPlayerComponent.pause();
		}
	}

	// Reset the audio preview if it loses sync with the video, it is not
	// expected to be compatible with FF/Rewind functionality
	public void resetPreview() {
		if (!isMerged && sp.getOwnAudio() != null) {
			audioIsFinished = false;
			audioPlayerComponent.stop();
			video.stop();
			pauseAll();
			playButton.doClick();
		}
	}

	// Various Getters and Setters used to access the GUI from BackEnd classes

	// Assigns the corresponding icon to the current playing status
	public void setPlayButton(Boolean play) {
		if (play) {
			playButton.setIcon(PLAYICON);
		} else {
			playButton.setIcon(PAUSEICON);
		}
	}

	public void setIsMerged(Boolean b) {
		isMerged = b;
	}

	public boolean getIsMerged() {
		return isMerged;
	}

	public void setAudioPlayer(AudioPlayer audio) {
		audioPlayerComponent = audio;
	}

	public void setAudioIsFinished(boolean b) {
		audioIsFinished = b;
	}

	public AudioConverter getAC() {
		return ac;
	}

	public Boolean getIsPlaying() {
		return isPlaying;
	}

	public void setIsPlaying(Boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public EmbeddedMediaPlayer getVideo() {
		return video;
	}

	public AudioPlayer getAudioComponent() {
		return audioPlayerComponent;
	}

	public Boolean getAudioIsFinished() {
		return audioIsFinished;
	}

	public JSlider getSoundCtrl() {
		return soundCtrl;
	}

	public ProgressSlider getProgress() {
		return progress;
	}

	public JButton getSelectVideoButton() {
		return button3;
	}

	// These 3 Setters/Getters redirect to the SidePanel instance
	// which is the primary storage for these variables
	public void setOwnAudio(String currentOwnAudioIn) {
		sp.setOwnAudio(currentOwnAudioIn);
	}

	public String getCurrentVideo() {
		return sp.getCurrentVideo();
	}

	public String getOriginalVideoPath() {
		return sp.getVideoPath();
	}

	public void setVisibleSidePanel() {
		if (rightpanel.isVisible()) {
			rightpanel.setVisible(false);
		} else {
			rightpanel.setVisible(true);
		}
	}

	// Pass the advancedFestival scheme information to the AdvancedFestival
	// instance
	public void updateScheme(String speed) {
		af.updateSchemeFile(speed);
	}

	public void updatePreview(String text) {
		af.updatePreviewScheme(text);
	}

}