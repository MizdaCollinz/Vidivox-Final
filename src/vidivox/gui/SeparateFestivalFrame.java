package vidivox.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;

import vidivox.backend.AudioConverter;
import vidivox.backend.DocumentLimit;

@SuppressWarnings("serial")
/**
 * Frame dedicated to the text-to-speech functionality of Vidivox Allows user to
 * preview any synthesized speech Allows user to output any synthesized speech
 * as an MP3 file Allows for changing of festival voice speed
 * 
 * @author Benjamin Collins BCOL602
 *
 */
public class SeparateFestivalFrame extends JFrame {

	private DefaultStyledDocument customDoc;
	private JLabel remainingCharacters;
	private JFrame thisFrame;
	private Process currentProcess;

	public SeparateFestivalFrame(final MediaPlayerMain frame) {

		setTitle("[Synthesize speech from text]");
		setSize(540, 250);
		getRootPane().setBorder(BorderFactory.createMatteBorder(0, 3, 0, 3, Color.WHITE));

		thisFrame = this;
		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);

		// Create new fonts with modified styles, while retaining the same font
		final Font defaultFONT = new JLabel().getFont();
		String defaultFont = defaultFONT.getName();

		final AudioConverter ac = frame.getAC();
		ac.setFestivalFrame(this);

		// Definition of Main Panels and Layouts

		JPanel corePanel = new JPanel(new BorderLayout(0, 5));
		corePanel.setBorder(new EmptyBorder(0, 5, 5, 5));
		JPanel botPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		botPanel.setBorder(new EmptyBorder(0, 0, 5, 0));

		// Primary label

		JLabel titleLabel2 = new JLabel("Create custom audio using text to speech");
		titleLabel2.setBorder(new EmptyBorder(5, 0, 0, 0));
		titleLabel2.setFont(new Font(defaultFont, Font.BOLD, 18));
		titleLabel2.setHorizontalAlignment(SwingConstants.LEFT);

		// Festival text box
		final JTextArea textBox2 = new JTextArea();
		remainingCharacters = new JLabel();
		remainingCharacters.setHorizontalAlignment(SwingConstants.LEFT);
		remainingCharacters.setFont(new Font(defaultFont, Font.PLAIN, 10));

		// Document used to restrict characters entered into textbox
		customDoc = new DefaultStyledDocument();
		customDoc.setDocumentFilter(new DocumentLimit(250));
		customDoc.addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateCount();

			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateCount();

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateCount();

			}

		});
		textBox2.setDocument(customDoc);
		updateCount();
		textBox2.setLineWrap(true);
		textBox2.setPreferredSize(new Dimension(350, 100));

		// Festival Speed setting
		JPanel festivalPanel = new JPanel(new BorderLayout());
		festivalPanel.add(textBox2, BorderLayout.CENTER);

		// Panel dedicated to voice speed
		JPanel speedRow = new JPanel(new FlowLayout(FlowLayout.TRAILING, 2, 0));
		JLabel newLabel = new JLabel("Speed of audio output:");
		newLabel.setFont(new Font(defaultFont, Font.BOLD, 12));

		// Combo box allows user to select between voice speeds
		String[] combos = { "Fast", "Normal", "Slow" };
		JComboBox<String> speedBox = new JComboBox<String>(combos);
		speedBox.setSelectedItem(combos[1]);
		speedBox.addActionListener(new ActionListener() {

			@Override
			// Update scheme files with any change in selection on combobox
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
				String speed = (String) comboBox.getSelectedItem();
				frame.updateScheme(speed);
			}

		});

		speedRow.add(newLabel);
		speedRow.add(speedBox);

		festivalPanel.add(speedRow, BorderLayout.SOUTH);

		// Festival reads out provided text as a preview
		JButton previewBtn = new JButton("Preview Text to Speech");
		previewBtn.setFont(new Font(defaultFont, Font.PLAIN, 11));

		previewBtn.setPreferredSize(new Dimension(180, 25));
		previewBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = textBox2.getText();

				if (text.isEmpty()) {
					JOptionPane.showMessageDialog(thisFrame, "Text box has been left blank", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					currentProcess = ac.convertToAudio(text);
				}
			}
		});

		// Button used to kill speaking process
		JButton cancelPreview = new JButton("Stop");
		cancelPreview.setFont(new Font(defaultFont, Font.PLAIN, 11));
		cancelPreview.setPreferredSize(new Dimension(100, 25));
		cancelPreview.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentProcess != null) {
					CancelProcess(currentProcess);
					currentProcess = null;
				}
			}
		});

		// Preview Panel includes the preview button and the Character count
		JPanel previewPanel = new JPanel(new BorderLayout());
		JPanel twoButtons = new JPanel(new FlowLayout());
		previewPanel.add(remainingCharacters, BorderLayout.WEST);
		twoButtons.add(previewBtn);
		twoButtons.add(cancelPreview);
		previewPanel.add(twoButtons, BorderLayout.EAST);
		previewPanel.setBorder(new EmptyBorder(3, 0, 0, 0));

		// Centre Panel includes practically all content except the final
		// options
		corePanel.add(titleLabel2, BorderLayout.NORTH);
		corePanel.add(festivalPanel, BorderLayout.CENTER);
		corePanel.add(previewPanel, BorderLayout.SOUTH);

		mainPanel.add(corePanel, BorderLayout.CENTER);

		// Bottom Panel - Contains Confirm/Cancel buttons for frame

		JButton okBtn = new JButton("Save to MP3 file");
		okBtn.setFont(new Font(defaultFont, Font.BOLD, 12));
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String festivalText = textBox2.getText();

				if (festivalText.isEmpty()) {
					JOptionPane.showMessageDialog(thisFrame, "Please enter text into the text area",
							"No text to convert", JOptionPane.ERROR_MESSAGE);
				} else {
					// convert festival text to wav file
					// get wav file path
					if (ac.convertToWav(festivalText)) {
						ac.wavToMp3();
						thisFrame.setVisible(false);
					}
				}

			}
		});
		okBtn.setPreferredSize(new Dimension(175, 25));

		JButton cnclBtn = new JButton("Cancel");
		cnclBtn.setFont(new Font(defaultFont, Font.BOLD, 12));
		cnclBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				thisFrame.setVisible(false);
			}
		});
		cnclBtn.setPreferredSize(new Dimension(175, 25));

		botPanel.add(okBtn);
		botPanel.add(cnclBtn);
		mainPanel.add(botPanel, BorderLayout.SOUTH);

	}

	// Updates the Remaining Characters count label.
	private void updateCount() {
		remainingCharacters.setText((250 - customDoc.getLength()) + " characters remaining");
	}

	// Kills ongoing process, only compatible with festival processes
	protected void CancelProcess(Process myprocess) {
		try {

			// Reflection hack from Assignment 2 used to acquire process id
			if (myprocess.getClass().getName().equals("java.lang.UNIXProcess")) {
				Field f;
				f = myprocess.getClass().getDeclaredField("pid");
				f.setAccessible(true);
				int pid = f.getInt(myprocess);

				// Acquire the process tree
				String cmd = "pstree -p " + pid + "| grep aplay";
				ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
				Process process = builder.start();
				InputStream stdout = process.getInputStream();
				BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				String processtree = stdoutBuffered.readLine();

				stdoutBuffered.close();

				// Search for and kill Aplay process to stop festival
				int location = processtree.indexOf("play(");
				int endlocation = processtree.indexOf(")", location);
				String newid = processtree.substring(location + 5, endlocation);
				cmd = "kill " + newid;
				builder = new ProcessBuilder("/bin/bash", "-c", cmd);
				process = builder.start();

			}

		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
