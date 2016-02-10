package vidivox.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import vidivox.backend.FileChooser;
import vidivox.backend.AudioPlayer;

@SuppressWarnings("serial")
/**
 * Separate frame dedicated to the selection of MP3 files to add to videos.
 * 
 * @author Benjamin Collins BCOL602
 *
 */
public class AddAudioFrame extends JFrame {

	private String ownAudio;

	// Components
	private AudioPlayer audioPlayerComponent;

	public AddAudioFrame(final MediaPlayerMain frame) {
		// Modify style of fonts, retain default font
		final Font defaultFONT = new JLabel().getFont();
		String defaultFont = defaultFONT.getName();

		// audio player components to preview the audio with video
		audioPlayerComponent = new vidivox.backend.AudioPlayer(frame);

		setTitle("[Input an MP3 file]");
		setSize(540, 220);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getRootPane().setBorder(BorderFactory.createMatteBorder(0, 3, 0, 3, Color.WHITE));
		setLocationRelativeTo(frame);

		final JPanel mainPanel = new JPanel(new BorderLayout(0, 10));

		// Ok/Cancel Panel
		JPanel botPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		botPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

		// Title Label + CentreTopPanel
		JPanel topPanel = new JPanel(new BorderLayout(0, 5));
		topPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
		// Textbox and BrowseButton
		JPanel browsePanel = new JPanel(new BorderLayout());

		JLabel titleLabel = new JLabel("Add existing MP3 to the video");
		titleLabel.setFont(new Font(defaultFont, Font.BOLD, 18));
		titleLabel.setHorizontalAlignment(SwingConstants.LEFT);

		// Contains path to audio file
		final JTextField textBox = new JTextField();
		textBox.setPreferredSize(new Dimension(300, 25));

		// Button used to call FileChooser to browse for mp3
		final JButton browseButton = new JButton("Browse");
		browseButton.setFont(new Font(defaultFont, Font.BOLD, 12));
		browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileChooser fc = new FileChooser(browseButton.getParent());
				FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 file", new String[] { "mp3" });
				fc.setFileFilter(filter);
				textBox.setText(fc.getPath());
			}
		});

		browsePanel.add(textBox, BorderLayout.CENTER);
		browsePanel.add(browseButton, BorderLayout.EAST);
		topPanel.add(titleLabel, BorderLayout.CENTER);
		topPanel.add(browsePanel, BorderLayout.SOUTH);

		// MIDDLE PANEL COMPONENTS
		JPanel middlePanel = new JPanel(new BorderLayout());
		// Delayed Audio Panel
		JPanel delayAudioPanel = new JPanel(new BorderLayout(3, 0));
		JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEADING));

		// Top Row of Delay Audio Panel
		JLabel titleDelay = new JLabel("Add audio at delayed position (mm:ss)");
		titleDelay.setFont(new Font(defaultFont, Font.BOLD, 18));
		final JCheckBox checkBox = new JCheckBox();

		topRow.add(titleDelay, BorderLayout.NORTH);
		topRow.add(checkBox, BorderLayout.CENTER);

		// Centrepiece of Delay Audio Panel

		JPanel middleRow = new JPanel(new BorderLayout());

		final JTextField numberBox = new JTextField();
		numberBox.setPreferredSize(new Dimension(300, 25));
		numberBox.setText("00:00");

		// Button to retrieve current position of the video, ease of adding
		// audio for user
		JButton setCurrentTime = new JButton("Current Video Position");
		setCurrentTime.setFont(new Font(defaultFont, Font.BOLD, 12));
		setCurrentTime.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String currentTime = frame.getCurrentTime();
				numberBox.setText(currentTime);
			}

		});

		middleRow.add(numberBox, BorderLayout.CENTER);
		middleRow.add(setCurrentTime, BorderLayout.EAST);

		// Add components to Delay Audio Panel
		delayAudioPanel.add(topRow, BorderLayout.CENTER);
		delayAudioPanel.add(middleRow, BorderLayout.SOUTH);

		middlePanel.add(delayAudioPanel, BorderLayout.CENTER);

		// NOTICE PANEL
		JLabel info = new JLabel("Note: This will preview the audio alongside the video.");
		info.setFont(new Font(defaultFont, Font.BOLD, 10));
		JLabel info2 = new JLabel("Press the \"Merge Selected Audio\" button to finalise the changes.");
		info2.setFont(new Font(defaultFont, Font.BOLD, 10));
		JPanel noticePanel = new JPanel(new GridLayout(2, 1));
		noticePanel.setBorder(new EmptyBorder(0, 5, 0, 5));
		noticePanel.add(info);
		noticePanel.add(info2);

		middlePanel.add(noticePanel, BorderLayout.SOUTH);

		// Prepares the selected audio track or tracks for previewing
		JButton okBtn = new JButton("Ok");
		okBtn.setFont(new Font(defaultFont, Font.BOLD, 12));
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// String festivalText = textBox2.getText();
				String audioText = textBox.getText();
				if (audioText.isEmpty()) {
					JOptionPane.showMessageDialog(mainPanel, "Please select an audio file", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				} else {

					// Start up preview of audio track alongside video
					if (checkBox.isSelected()) {
						// Split mm:ss into integers
						String chosenTime = numberBox.getText();
						String[] minSec = chosenTime.split(":", -1);

						// Error check if input wasnt at least split by a colon
						if (minSec.length != 2) {
							JOptionPane.showMessageDialog(mainPanel, "Please input a valid time in mm:ss format.",
									"Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
						// Convert input to milliseconds
						long milliseconds;

						try {
							int min = Integer.parseInt(minSec[0]);
							int sec = Integer.parseInt(minSec[1]);
							milliseconds = 60000 * min + 1000 * sec;

							// Return if audio is placed beyond video
							if (milliseconds >= frame.getVideo().getLength()) {
								JOptionPane.showMessageDialog(mainPanel,
										"This time is past the end of the selected video.", "Error",
										JOptionPane.ERROR_MESSAGE);
								return;
							}

							ownAudio = frame.getAC().delayedPreview(audioText, milliseconds);

							// If integers can't be parsed from input, return
						} catch (NumberFormatException x) {
							JOptionPane.showMessageDialog(mainPanel, "Please input a valid time in mm:ss format.",
									"Error", JOptionPane.ERROR_MESSAGE);
							return;
						}

					} else {
						ownAudio = audioText;
						JOptionPane.showMessageDialog(mainPanel, "This track will now be previewed over the video.");
					}
					// Enable frame audio players, Autoplay selected audio track
					// as a preview

					frame.setOwnAudio(ownAudio);
					frame.setAudioPlayer(audioPlayerComponent);
					audioPlayerComponent.start(ownAudio + "");

				}
				// Set IsMerged to false, there is now new audio to merge
				frame.setIsMerged(false);
				// New incomplete audio preview, Audio is not finished.
				frame.setAudioIsFinished(false);
				frame.playNewVideo(frame.getCurrentVideo());
				dispose();
			}
		});
		okBtn.setPreferredSize(new Dimension(100, 25));

		JButton cnclBtn = new JButton("Cancel");
		cnclBtn.setFont(new Font(defaultFont, Font.BOLD, 12));
		cnclBtn.setPreferredSize(new Dimension(100, 25));
		cnclBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		botPanel.add(okBtn);
		botPanel.add(cnclBtn);

		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(middlePanel, BorderLayout.CENTER);
		mainPanel.add(botPanel, BorderLayout.SOUTH);
		setContentPane(mainPanel);
		setVisible(true);
	}

	public AudioPlayer getAudioPlayer1() {
		return audioPlayerComponent;
	}

}
