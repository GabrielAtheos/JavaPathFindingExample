/*Map
 *Gabriel Sturtevant
 *11/08/2015
 */

import static java.lang.StrictMath.sqrt;
import java.io.FileNotFoundException;
import java.util.logging.Logger;
import static java.awt.Color.*;
import java.io.BufferedReader;
import SimulationFramework.*;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileReader;
import java.util.*;
import java.awt.*;
import java.text.*;

/**
 * @author Gabriel Sturtevant
 */
public final class Map
{

	SimFrame simFrame = null;
	AnimatePanel animatePanel = null;
	private ArrayList<Bot> bot;
	private int xPosCurrent = 0;
	private int yPosCurrent = 0;
	private int xDestinationPosition = 0;
	private int yDestinationPosition = 0;
	private Player player;
	private static String finalStartHash;
	private String destinationHash = null;
	private final HashMap<String, WayPoint> hashmap = new HashMap<>();
	private WayPoint waypoint;
	public WayPoint startWaypoint;
	public WayPoint currentWaypoint;
	public WayPoint endWaypoint;
	public final WayPoint finalEndWaypoint;
	public final WayPoint finalStartWaypoint;
	private static final int SIZE = 5;
	private static String path;
	private int numberOfLines;
	private int numberOfMaps = 0;
	private int totalGold = 0;
	private int numberOfCities = 0;
	public static final int MAXVALUE = Integer.MAX_VALUE;
	public String status = null;
	private int moves = 0;
	private boolean hasMap = false;
	public boolean found = false;
	public boolean hasPath = false;
	public boolean atDestination = false;
	public boolean noSolution = false;
	public boolean outOfStrength = false;
	private final NumberFormat numberFormat = new DecimalFormat("#0.00");
	Stack<WayPoint> st = new Stack<>();
	Comparator<WayPoint> comparator = new compareTo();
	PriorityQueue<WayPoint> queue = new PriorityQueue<>(536, comparator);
	HashMap<String, WayPoint> closed = new HashMap<>();

	/**
	 * Constructs Map. Calls methods which load all details into the animated
	 * map and then listens for mouse clicks to determine placement and
	 * destination of the player bot
	 *
	 * @param sf SimFrame instance from Sturtevant282P1
	 * @param foo AnimatePanel instance from Sturtevant282P1
	 * @param bar passes in the name of the text file used to import the
	 * waypoints
	 */
	public Map(SimFrame sf, AnimatePanel foo, String bar)
	{
		simFrame = sf;
		animatePanel = foo;
		path = bar;

		numberOfLines = numberOfLines();

		status = "Create HashMap <" + numberOfLines + "> ";

		openFile();
		drawPoints();

		status += "Cities <" + numberOfCities + "> Gold <" + totalGold
				+ "> Maps <" + numberOfMaps + ">";
		simFrame.statusReport(status);
		status = null;

		createBot();
		finalStartHash = xPosCurrent + " " + yPosCurrent;
		destinationHash = xDestinationPosition + " " + yDestinationPosition;
		currentWaypoint = startWaypoint = hashmap.get(finalStartHash);
		endWaypoint = hashmap.get(destinationHash);
		finalEndWaypoint = endWaypoint;
		finalStartWaypoint = currentWaypoint;
		//setHeuristics();

		status = "Start (<" + xPosCurrent + "," + yPosCurrent + ">), Stop (<"
				+ xDestinationPosition + "," + yDestinationPosition
				+ ">), Player <" + player.getStrength() + "> $ <"
				+ player.getWealth() + ">";
		simFrame.statusReport(status);
		status = null;

		queue.add(currentWaypoint);
	}

	/**
	 * Found map is used once the player comes across a map that leads to
	 * treasure that has not already be retrieved This method is used to reset
	 * important control flags as well as establish starting and stopping
	 * WayPoints, it also forces a recalculation of the heuristics for all
	 * waypoints. In addition it clears out the open and closed sets, and the
	 * movement stack, it also clears all temporary waypoints from the
	 * simulation framework
	 */
	private void foundMap()
	{
		hasPath = false;
		found = false;
		hasMap = true;
		while (!st.isEmpty())
		{
			st.pop();
		}
		animatePanel.clearTemporaryDrawables();

		animatePanel.addTemporaryDrawable(new Marker(xPosCurrent,
				yPosCurrent, blue, 4));

		String newHash = currentWaypoint.getMapX() + " "
				+ currentWaypoint.getMapY();
		endWaypoint = hashmap.get(newHash);

		animatePanel.addTemporaryDrawable(new Marker(endWaypoint.getX(),
				endWaypoint.getY(), blue, 4));

		startWaypoint = currentWaypoint;

		hashmap.entrySet().stream().forEach((e) ->
		{
			hashmap.get(e.getKey()).currentCost = 0;
		});

		//setHeuristics();

		while (!closed.isEmpty())
		{
			closed.clear();
		}
		while (!queue.isEmpty())
		{
			queue.poll();
		}
		queue.add(currentWaypoint);
	}

	/**
	 * Similar to the foundMap method, found treasure exists to reset control
	 * flags, empty data structures and recalculate heuristics values
	 */
	private void foundTreasure()
	{
		hasMap = false;
		hasPath = false;
		found = false;
		while (!st.isEmpty())
		{
			st.pop();
		}
		animatePanel.clearTemporaryDrawables();
		animatePanel.addTemporaryDrawable(new Marker(xPosCurrent,
				yPosCurrent, blue, 4));
		startWaypoint = currentWaypoint;
		while (!closed.isEmpty())
		{
			closed.clear();
		}
		while (!queue.isEmpty())
		{
			queue.poll();
		}
		queue.add(currentWaypoint);
		endWaypoint = finalEndWaypoint;
		animatePanel.addTemporaryDrawable(new Marker(endWaypoint.getX(),
				endWaypoint.getY(), blue, 4));
		hashmap.entrySet().stream().forEach((e) ->
		{
			hashmap.get(e.getKey()).currentCost = 0;
		});
		//setHeuristics();
	}

	/**
	 * setHeuristics cycles through all the waypoints in the HashMap using
	 * functional methodology and calculates the absolute distance between each
	 * waypoint and the destination waypoint
	 */
	private void setHeuristics()
	{
		hashmap.entrySet().stream().forEach((e) ->
		{
			hashmap.get(e.getKey()).setHeuristic(endWaypoint);
		});
	}

	/**
	 * Utilizing functional methodology, drawPoints() visits every member of
	 * hashmap and looks at each WayPoint's attributes and places a permanent
	 * marker on the map with the appropriate color and size.
	 */
	private void drawPoints()
	{
		hashmap.entrySet().stream().forEach((e) ->
		{
			if (hashmap.get(e.getKey()).getMapX() > 0)
			{
				numberOfMaps++;
				animatePanel.addPermanentDrawable(new Marker(hashmap
						.get(e.getKey()).getX(), hashmap.get(e.getKey())
						.getY(), magenta, SIZE));

			} else if (hashmap.get(e.getKey()).getCity() > 0)
			{
				numberOfCities++;
				animatePanel.addPermanentDrawable(new Marker(hashmap
						.get(e.getKey()).getX(), hashmap.get(e.getKey())
						.getY(), cyan, SIZE));

			} else if (hashmap.get(e.getKey()).getGold() > 0)
			{
				totalGold += hashmap.get(e.getKey()).getGold();
				animatePanel.addPermanentDrawable(new Marker(hashmap
						.get(e.getKey()).getX(), hashmap.get(e.getKey())
						.getY(), yellow, SIZE));

			} else
			{
				animatePanel.addPermanentDrawable(new Marker(hashmap
						.get(e.getKey()).getX(), hashmap.get(e.getKey())
						.getY(), black));
			}
		});
	}

	/**
	 * During the A* search algorithm, each waypoint is linked to the it's
	 * parent waypoint by a stored hash value. returnPath() looks at the current
	 * waypoint, pushes it into a stack "st" and then changes the current
	 * waypoint to the parent waypoint then repeats this process until the
	 * current waypoint is at the waypoint that the search algorithm started at
	 */
	public void returnPath()
	{
		moves = 0;

		while (!currentWaypoint.equals(startWaypoint))
		{
			moves++;
			st.push(currentWaypoint);
			currentWaypoint = closed.get(currentWaypoint.parentHash);
		}

		status = "Path" + startWaypoint.getXY() + " to" + endWaypoint.getXY()
				+ ", length <" + moves + ">";
		simFrame.statusReport(status);
		animatePanel.repaint();
		st.add(currentWaypoint);
		hasPath = true;
	}

	/**
	 * This method controls the bot's movement through the stack that contains
	 * the path to the end waypoint. If it finds gold, the player takes the gold
	 * if the player finds a city, it buys strength, if the player finds a map,
	 * the goTo() method will initiate stopping the character to redirect the
	 * character to where the map leads, if the gold at that location has not
	 * already been collected
	 *
	 * @return a boolean control variable that let's the Sturtevant282P2 class
	 * know whether or not to continue calling the goTo() method
	 */
	public boolean goTo()
	{
		if (atDestination)
		{
			return atDestination;
		}

		status = null;
		currentWaypoint.setGoldGone();
		player.subtractStrength(currentWaypoint.getDistance(st.peek()));
		currentWaypoint = st.pop();
		if(player.getStrength()<0)
			player.zeroStrength();
		
		if ((currentWaypoint.getGold() > 0))
		{
			status = "Gold (<" + xPosCurrent + ">,<" + yPosCurrent + ">) $ <"
					+ currentWaypoint.getGold() + ">, Player <"
					+ numberFormat.format(player.getStrength())
					+ "> $ <" + player.getWealth() + ">";

			player.addWealth(currentWaypoint.getGold());
			currentWaypoint.setGold();
		}
		if (currentWaypoint.getCity() > 0 && player.getStrength() >= currentWaypoint.getCity())
		{
			status = "City (<" + xPosCurrent + ">,<" + yPosCurrent + ">) $ <"
					+ currentWaypoint.getCity() + ">, Player <"
					+ numberFormat.format(player.getStrength())
					+ "> $ <" + player.getWealth() + ">";

			player.addStrength(currentWaypoint.getCity());
			player.subtractWealth(currentWaypoint.getCity());
			currentWaypoint.setCity();
		}
		if ((currentWaypoint.getMapX() > 0 && !currentWaypoint.getVisitedMap())
				&& !hasMap && (!hashmap.get(currentWaypoint.getMapXY())
				.getGoldGone()))
		{
			status = "Map (<" + xPosCurrent + ">,<" + yPosCurrent
					+ ">) Treasure (<" + currentWaypoint.getMapX()
					+ ">,<" + currentWaypoint.getMapY() + "Player <"
					+ numberFormat.format(player.getStrength()) + "> $ <"
					+ player.getWealth() + ">";

			moveBot(currentWaypoint.getHashCode());
			currentWaypoint.setVisitedMap();
			foundMap();
			return false;
		}
		moveBot(currentWaypoint.getHashCode());

		if (status == null)
		{
			status = "Position" + currentWaypoint.getXY() + "Player <"
					+ numberFormat.format(player.getStrength())
					+ "> $ <" + player.getWealth() + ">";
		}
		simFrame.statusReport(status);

		if (currentWaypoint.equals(endWaypoint) && hasMap)
		{
			foundTreasure();
			return false;
		} else if (currentWaypoint.equals(endWaypoint))
		{
			if (endWaypoint.equals(finalEndWaypoint))
			{
				status = "Success, goal" + finalEndWaypoint.getXY()
						+ "Player <" + numberFormat.format(player.getStrength())
						+ "> $ <" + player.getWealth() + "> ";
				simFrame.statusReport(status);
			}
			return atDestination = true;
		} else
		{
			return true;
		}
	}

	/**
	 * aStar() uses a 'open' priorityQueue and a 'closed' hashmap to sort
	 * through waypoints based on the distance from the start point to the
	 * current waypoint as well as a heuristic value from the current waypoint
	 * to the destination waypoint
	 *
	 *
	 * @return boolean to state whether a path to the current destination has
	 * already been found
	 */
	public boolean aStar()
	{
		if (found)
		{
			return true;
		}

		if (!queue.isEmpty())
		{
			currentWaypoint = queue.poll();
		}
		
		setHeuristics();
		
		closed.put(currentWaypoint.getHashCode(), currentWaypoint);

		animatePanel.addTemporaryDrawable(new Marker(currentWaypoint.getX(),
				currentWaypoint.getY(), gray, 2));

		if (currentWaypoint.equals(endWaypoint))
		{
			returnPath();
			return found = true;
		}

		for (int i = 0; i < currentWaypoint.getN(); i++)
		{
			WayPoint temp = hashmap.get(currentWaypoint.getNeighborHash(i));
			if (!closed.containsKey(temp.getHashCode()) && !(queue.contains(temp)))
			{
				temp.setCurrentCost(currentWaypoint);

				temp.parentHash = currentWaypoint.getHashCode();

				animatePanel.addTemporaryDrawable(new Marker(temp.getX(),
						temp.getY(), white, 3));

				queue.add(temp);
			}
		}

		if (queue.isEmpty())
		{
			noSolution = true;
		}
		return false;
	}

	/**
	 * Creates a player bot, and marks both the start point and destination
	 * point of the player bot. Players mouse clicks are rounded in both x and y
	 * coordinates to the nearest multiple of 20. Start point marker is green,
	 * end point marker is red, with the player's point and resultant path being
	 * red
	 */
	public void createBot()
	{
		status = null;

		simFrame.setStatus("Player Position");

		//Set start position and make player bot
		simFrame.waitForMousePosition();
		double tempXPosCurrent = simFrame.mousePosition.getX();
		double tempYPosCurrent = simFrame.mousePosition.getY();
		double max = Double.MAX_VALUE;
		int xStore = 0;
		int yStore = 0;
		for (HashMap.Entry<String, WayPoint> e : hashmap.entrySet())
		{
			double distance;
			distance = sqrt((hashmap.get(e.getKey()).getX() - tempXPosCurrent)
					* (hashmap.get(e.getKey()).getX() - tempXPosCurrent)
					+ (hashmap.get(e.getKey()).getY() - tempYPosCurrent)
					* (hashmap.get(e.getKey()).getY() - tempYPosCurrent));
			if (distance < max)
			{
				max = distance;
				xStore = hashmap.get(e.getKey()).getX();
				yStore = hashmap.get(e.getKey()).getY();
			}
		}

		xPosCurrent = xStore;
		yPosCurrent = yStore;
		animatePanel.addTemporaryDrawable(new Marker(xPosCurrent,
				yPosCurrent, blue, 4));
		makeGabesBot("Red", xStore, yStore, red);
		animatePanel.repaint();

		//set destination
		simFrame.waitForMousePosition();
		double tempXDestinationPosition = simFrame.mousePosition.getX();
		double tempYDestinationPosition = simFrame.mousePosition.getY();

		max = Double.MAX_VALUE;
		xStore = 0;
		yStore = 0;
		for (HashMap.Entry<String, WayPoint> e : hashmap.entrySet())
		{
			double distance;
			distance = sqrt((hashmap.get(e.getKey()).getX()
					- tempXDestinationPosition) * (hashmap.get(e.getKey()).getX()
					- tempXDestinationPosition)
					+ (hashmap.get(e.getKey()).getY() - tempYDestinationPosition)
					* (hashmap.get(e.getKey()).getY()
					- tempYDestinationPosition));
			if (distance < max)
			{
				max = distance;
				xStore = hashmap.get(e.getKey()).getX();
				yStore = hashmap.get(e.getKey()).getY();
			}
		}

		xDestinationPosition = xStore;
		yDestinationPosition = yStore;

		animatePanel.addTemporaryDrawable(new Marker(xStore, yStore, blue, 4));
	}

	/**
	 * makeGabesBot creates a new instance of the player class, creates a bot
	 * which moves around the map and shows the path followed It also creates a
	 * new array list to store the bot in for easy access without needing to
	 * pass the bot around. Next it adds the bot to the animated window
	 *
	 * @param name Name of the bot
	 * @param x X coordinate of the initial position of the bot
	 * @param y Y coordinate of the initial position of the bot
	 * @param color Color of the bot and the path it leaves behind
	 */
	public void makeGabesBot(String name, int x, int y, Color color)
	{
		player = new Player(name, x, y, color);
		Bot b = player;
		bot = new ArrayList<>();
		bot.add(b);
		animatePanel.addBot(b);
	}

	/**
	 * Receives the hash value for the waypoint instance for the player bot to
	 * move to, moves the bot and returns a value of one.
	 *
	 * @param foo Hash value String consisting of the concatenation of the x and
	 * y values with a space in between
	 */
	public void moveBot(String foo)
	{
		Scanner sin = new Scanner(foo);
		int x;
		int y;
		x = sin.nextInt();
		y = sin.nextInt();
		Bot aBot = bot.get(0);
		aBot.moveTo(x, y);
		xPosCurrent = x;
		yPosCurrent = y;
	}

	/**
	 *
	 * @return numberOfLines: which contains the integer value of how many lines
	 * are present inside of the file being read
	 */
	private int numberOfLines()
	{
		try
		{
			FileReader fr = new FileReader(path);
			try (BufferedReader br = new BufferedReader(fr))
			{

				numberOfLines = 0;

				while (br.readLine() != null)
				{
					numberOfLines++;
				}
			}

		} catch (IOException e)
		{
			System.out.println(e.getCause());
		}

		return numberOfLines;
	}

	/**
	 * Reads waypoints from file places them on the map
	 */
	public void openFile()
	{
		String textData;
		FileReader fr = null;

		try
		{
			fr = new FileReader(path);
		} catch (FileNotFoundException ex)
		{
			Logger.getLogger(path);
		}
		try (BufferedReader textReader = new BufferedReader(fr))
		{
			numberOfLines = numberOfLines();

			for (int i = 0; i < numberOfLines; i++)
			{
				textData = textReader.readLine();
				waypoint = new WayPoint();
				waypoint.setInfo(textData);
				hashmap.put(waypoint.getHashCode(), waypoint);

				for (int j = 0; j < waypoint.getN(); j++)
				{

					Connector c = new Connector(waypoint.getX(), waypoint.getY(),
							waypoint.getNeighborX(j), waypoint.getNeighborY(j),
							black);
					animatePanel.addPermanentDrawable(c);

				}
			}
		} catch (IOException e)
		{
			System.out.println(e.getCause());
		}
	}

	/**
	 * Override class used with sorting the PriorityQueue
	 */
	private static class compareTo implements Comparator<WayPoint>
	{

		@Override
		public int compare(WayPoint a, WayPoint b)
		{
			if ((a.getValue()) == (b.getValue()))
			{
				return 0;
			} else if ((a.getValue()) > (b.getValue()))
			{
				return 1;
			} else
			{
				return -1;
			}
		}
	}
}
