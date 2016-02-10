package vidivox.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import vidivox.backend.AudioConverter;

@SuppressWarnings("serial")
/**
 * Frame dedicated to the Removal of audio from videos, recently added audio or
 * all audio entirely
 * 
 * @author Benjamin Collins BCOL602
 *
 */
public class StripAudioFrame extends JFrame {

	String outputFolder;

	public StripAudioFrame(final MediaPlayerMain frame) {

		setTitle("Strip Audio");
		setSize(250, 100);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getRootPane().setBorder(BorderFactory.createMatteBorder(0, 3, 3, 3, Color.WHITE));
		setLocationRelativeTo(frame);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);

		outputFolder = "CustomVidivoxFolder";

		final Font defaultFONT = new JLabel().getFont();
		String defaultFont = defaultFONT.getName();

		final AudioConverter ac = new AudioConverter(this);

		final JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		JPanel botPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		botPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

		// sets default selected radio button to remove all audio option
		final JRadioButton rmAudio = new JRadioButton("Remove all audio", true);
		rmAudio.setFont(new Font(defaultFont, Font.BOLD, 12));
		JRadioButton originalVidAudio = new JRadioButton("Revert to pre-merged video", false);
		originalVidAudio.setFont(new Font(defaultFont, Font.BOLD, 12));

		buttonPanel.add(rmAudio);
		buttonPanel.add(originalVidAudio);

		// Grouping of the Radio button options
		ButtonGroup group = new ButtonGroup();
		group.add(rmAudio);
		group.add(originalVidAudio);

		mainPanel.add(buttonPanel, BorderLayout.NORTH);

		// OK Button follows through with the selected radio button option
		JButton okBtn = new JButton("Ok");
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Remove all audio option is selected and followed through
				if (rmAudio.isSelected()) {
					// Checks if video has audio to remove
					if ((ac.isAudioless(frame.getCurrentVideo().replace(" ", "\\ ")) == null)
							|| (ac.isAudioless(frame.getCurrentVideo().replace(" ", "\\ ")).equals(""))) {
						JOptionPane.showMessageDialog(frame, "There is no audio available to be stripped");
					} else {
						String output = JOptionPane.showInputDialog(frame,
								"Name the audioless video, please include the current extension");

						File f = new File(outputFolder + "/" + output);

						// Ensures output filename is valid
						if (output == null) {
							return;
						} else if (output.length() == 0) {
							JOptionPane.showMessageDialog(frame, "No name was selected");
						} else if (f.exists()) {
							JOptionPane.showMessageDialog(frame, "This file already exists.");
						} else {
							// Strips the audio and autoplays audioless video
							ac.removeAudio(frame.getCurrentVideo(), output);
							frame.playNewVideo(outputFolder + "/" + output);
						}
						frame.setEnabled(true);
						dispose();
					}

					// Revert to original video is selected and swapped to
				} else {
					// Reverts to video selection before merging
					if (frame.getIsMerged() == true) {
						File file = new File(frame.getCurrentVideo());
						file.delete();
						frame.playNewVideo(frame.getOriginalVideoPath());
						frame.setEnabled(true);
						dispose();
					} else {
						JOptionPane.showMessageDialog(frame,
								"There has been no new audio merged with the original video.");
					}
				}
			}
		});
		okBtn.setPreferredSize(new Dimension(100, 25));

		// Cancel button is the only way to exit this frame without completing
		// other actions, reenables the main frame
		JButton cnclBtn = new JButton("Cancel");
		cnclBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setEnabled(true);
				dispose();
			}
		});
		cnclBtn.setPreferredSize(new Dimension(100, 25));

		botPanel.add(okBtn);
		botPanel.add(cnclBtn);

		mainPanel.add(botPanel, BorderLayout.SOUTH);

		add(mainPanel);
		setVisible(true);
	}

	public void setOutputFolder(String outputFolderName) {
		outputFolderName.replace(" ", "\\ ");
		outputFolder = outputFolderName;
	}
}
