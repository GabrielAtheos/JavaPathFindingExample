/*
 Sturtevant282P1
 Gabriel Sturtevant
 10/11/2015

 Modified from EmptySimFrame
 */

import java.awt.event.*;
import javax.swing.*;
import SimulationFramework.*;

/**
 * EmptySimFrame is the simulation's main class (simulation app) that is a
 * subclass of SimFrame.
 * <p>
 *
 * 282 Simulation Framework applications must have a subclass of SimFrame that
 * also has a main method. The simulation app can make the appropriate author
 * and usage "help" dialogs, override setSimModel() and simulateAlgorithm()
 * abstract methods inherited from SimFrame. They should also add any specific
 * model semantics and actions.
 *
 * <p>
 *
 * The simulated algorithm is defined in simulateAlgorithm().
 *
 * <p>
 * EmptySimFrame UML class diagram
 * <p>
 * <Img align left src="../UML/EmptySimFrame.png">
 *
 * @since 8/12/2013
 * @version 3.0
 * @author G. M. Barnes
 */
public class Sturtevant282P2 extends SimFrame
{

	// eliminate warning @ serialVersionUID
	private static final long serialVersionUID = 42L;
	// GUI components for application's menu
	/**
	 * the simulation application
	 */
	private Sturtevant282P2 app;

	private final String path = "waypoint.txt";

	private Map map = null;

	public static void main(String args[])
	{
		Sturtevant282P2 app = new Sturtevant282P2("Sturtevant282P2", "terrain282.png");
		app.start();  // start is inherited from SimFrame
	}

	/**
	 * Make the application: create the MenuBar, "help" dialogs,
	 * @param frameTitle
	 * @param imageFile
	 */
	public Sturtevant282P2(String frameTitle, String imageFile)
	{
		super(frameTitle, imageFile);
		// create menus
		JMenuBar menuBar = new JMenuBar();
		// set About and Usage menu items and listeners.
		aboutMenu = new JMenu("About");
		aboutMenu.setMnemonic('A');
		aboutMenu.setToolTipText(
				"Display informatiion about this program");
		// create a menu item and the dialog it invoke 
		usageItem = new JMenuItem("Usage");
		authorItem = new JMenuItem("Author");
		usageItem.addActionListener(// anonymous inner class event handler
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						JOptionPane.showMessageDialog(Sturtevant282P2.this,
								"Second project for Comp282 \n\n"
								+ "This project implements \n"
								+ "PriorityQueues graphs and \n"
								+ "the A* path finding algorithm",
								"Usage", // dialog window's title
								JOptionPane.PLAIN_MESSAGE);
					}
				}
		);
		// create a menu item and the dialog it invokes
		authorItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				JOptionPane.showMessageDialog(Sturtevant282P2.this,
						"Gabriel Sturtevant \n"
						+ "gabriel.sturtevant.797@my.csun.edu \n"
						+ "Comp 282",
						"Author", // dialog window's title
						JOptionPane.INFORMATION_MESSAGE,
						//  author's picture 
						new ImageIcon("gabriel.png"));
			}
		}
		);
		// add menu items to menu 
		aboutMenu.add(usageItem);   // add menu item to menu
		aboutMenu.add(authorItem);
		menuBar.add(aboutMenu);
		setJMenuBar(menuBar);
		validate();  // resize layout managers
		// construct the application specific variables
	}

	/**
	 * Initializes an instance of Map which also animates the window and sets
	 * all of the map attributes
	 */
	public void setSimModel()
	{
		// set any initial visual Markers or Connectors
		// get any required user mouse clicks for positional information.
		// initialize any algorithm halting conditions (ie, number of steps/moves).

		map = new Map(this, animatePanel, path);

		setStatus("Initial state of simulation");
	}

	public synchronized void simulateAlgorithm()
	{
		while (runnable())
		{
			animatePanel.repaint();

			if (!map.noSolution)
			{
				if (!map.aStar()){}
				else map.goTo();
			} else
			{
				this.statusReport("No path" + map.finalStartWaypoint.getXY()
						+ "to" + map.finalEndWaypoint.getXY());
				return;
			}
			checkStateToWait();
		}
	}
}
