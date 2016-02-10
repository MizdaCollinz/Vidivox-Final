package vidivox.backend.eventlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import vidivox.gui.MediaPlayerMain;

/**
 * MenuBar at the top of the Main JFrame Includes a File Menu and View Menu
 * 
 * @author Benjamin Collins BCOL602
 *
 */
public class MenuBar {
	JMenuBar menuBar;
	JMenu menu, viewMenu;
	JMenuItem menuItem, menuItem2, menuItem3, menuItem4, menuItem5;

	public MenuBar(final MediaPlayerMain frame) {
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		menuBar.add(menu);

		// File Menu
		menuItem = new JMenuItem("Reset Audio Preview", KeyEvent.VK_R);
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.resetPreview();
			}

		});

		// Alternate SelectVideo button when the sidepanel is hidden
		menuItem3 = new JMenuItem("Select Video", KeyEvent.VK_S);
		menuItem3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getSelectVideoButton().doClick();
			}

		});

		menu.add(menuItem3);
		menu.add(menuItem);

		// View Menu
		viewMenu = new JMenu("View");
		menuBar.add(viewMenu);

		// Toggle visibility of the SidePanel, for more standard media player
		// use, wider media component
		menuItem2 = new JMenuItem("Toggle Side Panel", KeyEvent.VK_T);
		menuItem2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisibleSidePanel();
			}

		});
		viewMenu.add(menuItem2);

		// Maximise the Media Player
		menuItem4 = new JMenuItem("Maximise Media Player", KeyEvent.VK_M);
		menuItem4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

			}

		});
		viewMenu.add(menuItem4);

		// Revert from Maximised, Reset to default size after resizing
		menuItem5 = new JMenuItem("Default Window Size", KeyEvent.VK_D);
		menuItem5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setExtendedState(JFrame.NORMAL);
				frame.setSize(1024, 560);

			}

		});
		viewMenu.add(menuItem5);

		frame.setJMenuBar(menuBar);
	}

}
