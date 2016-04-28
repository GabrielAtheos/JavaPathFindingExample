/*WayPoint
 *Gabriel Sturtevant
 *11/08/2015
 */

import java.util.ArrayList;
import java.util.Scanner;
import SimulationFramework.*;
import java.awt.Color;
import static java.lang.StrictMath.sqrt;

/**
 * @author gabriel
 */
public class WayPoint extends Marker
{

	public static final int MAX_SIZE = 24;
	private final String SPACE = " ";
	private String hashCode;

	//Map data variables//
	private int x;
	private int y;
	private int height;
	private int city;
	private int gold;
	private int mapX;
	private int mapY;
	private int n;
	private int[] nX;
	private int[] nY;
	private ArrayList<String> nXY;
	private boolean beenHere = false;
	private boolean beenChecked = false;
	private boolean visitedMap = false;
	public double[] distances;
	public double heuristic;
	public double currentCost = 0;
	public double moveCost = 0;
	public String parentHash = null;
	private boolean goldGone = false;
	private double value = 0;

	/**
	 * Empty constructor
	 */
	public WayPoint()
	{
		super(0, 0, Color.black);
	}

	/**
	 * @param foo Given line from WayPoint text file that contains map
	 * attributes
	 */
	public void setInfo(String foo)
	{

		Scanner sin = new Scanner(foo);
		nXY = new ArrayList<>();

		x = sin.nextInt();
		y = sin.nextInt();
		height = sin.nextInt();
		city = sin.nextInt();
		gold = sin.nextInt();
		mapX = sin.nextInt();
		mapY = sin.nextInt();
		n = sin.nextInt();

		distances = new double[n];

		hashCode = Integer.toString(x) + SPACE + Integer.toString(y);

		nX = new int[n];
		nY = new int[n];

		for (int i = 0; i < n; i++)
		{
			nX[i] = sin.nextInt();
			nY[i] = sin.nextInt();
			nXY.add(i, Integer.toString(nX[i]) + SPACE + Integer.toString(nY[i]));
			distances[i] = sqrt((x - nX[i]) * (x - nX[i]) + (y - nY[i]) * (y - nY[i]));
		}
	}

	/**
	 * Returns the hashcode for this instance of waypoint
	 *
	 * @return String
	 */
	public String getHashCode()
	{
		return hashCode;
	}

	/**
	 * Returns the X-coordinate for this instance of waypoint
	 *
	 * @return the X-coordinate for this instance of waypoint
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Returns the Y-coordinate for this instance of waypoint
	 *
	 * @return the Y-coordinate for this instance of waypoint
	 */
	public int getY()
	{
		return y;
	}

	/**
	 * Returns a String with location info in the form of "('<'x'>','<'y'>')"
	 *
	 * @return String
	 */
	public String getXY()
	{
		return " (<" + Integer.toString(x) + ">,<" + Integer.toString(y) + ">) ";
	}

	/**
	 * Returns the height for this instance of waypoint
	 *
	 * @return the height for this instance of waypoint
	 */
	public int getHeight()
	{
		return height;
	}

	/**
	 * Returns the number of neighbors for this instance of waypoint
	 *
	 * @return int the number of neighbors for this instance of waypoint
	 */
	public int getN()
	{
		return n;
	}

	/**
	 * Returns value of gold for this instance of waypoint
	 *
	 * @return int value of gold for this instance of waypoint
	 */
	public int getGold()
	{

		return gold;
	}

	/**
	 * This method is called when a player passes a waypoint with a gold value
	 */
	public void setGold()
	{
		gold = 0;
	}

	/**
	 * Returns the value of city for this instance of waypoint
	 *
	 * @return int the value of city for this instance of waypoint
	 */
	public int getCity()
	{
		return city;
	}

	/**
	 * This method is called when a player purchases strength from a given
	 * waypoint
	 */
	public void setCity()
	{
		city = 0;
	}

	/**
	 * Returns the value for MapX for this instance of waypoint
	 *
	 * @return int value for MapX for this instance of waypoint
	 */
	public int getMapX()
	{
		return mapX;
	}

	/**
	 * Returns the value for MapX for this instance of waypoint
	 *
	 * @return int value for MapX for this instance of waypoint
	 */
	public int getMapY()
	{
		return mapY;
	}

	/**
	 * Returns the x value of the position coordinate of the neighbor stored at
	 * the index i of the neighbors x value array
	 *
	 * @param i
	 * @return int x value of the position coordinate of the neighbor stored at
	 * the index i of the neighbors x value array
	 */
	public int getNeighborX(int i)
	{
		return nX[i];
	}

	/**
	 * Returns the y value of the position coordinate of the neighbor stored at
	 * the index i of the neighbors y value array
	 *
	 * @param i
	 * @return int y value of the position coordinate of the neighbor stored at
	 * the index i of the neighbors y value array
	 */
	public int getNeighborY(int i)
	{
		return nY[i];
	}

	/**
	 * Returns the hashvalue of the position coordinate of the neighbor stored
	 * at the index i of the neighbors hashvalue array list
	 *
	 * @param i
	 * @return String hashvalue of the position coordinate of the neighbor
	 * stored at the index i of the neighbors hashvalue array list
	 */
	public String getNeighborHash(int i)
	{
		return nXY.get(i);
	}

	/**
	 * Set value once this waypoint has been visited
	 */
	public void setBeenHere()
	{
		beenHere = true;
	}

	/**
	 * Returns whether this waypoint has been visited
	 *
	 * @return boolean flag stating whether this waypoint has been visited
	 */
	public boolean getBeenHere()
	{
		return beenHere;
	}

	/**
	 * Sets boolean value to see if the point has been looked at
	 */
	public void setBeenChecked()
	{
		beenChecked = true;
	}

	/**
	 *
	 * @return boolean stating whether or not the waypoint has been looked at
	 */
	public boolean getBeenChecked()
	{
		return beenChecked;
	}

	/**
	 * Calculates and stores a distance from this waypoint to the passed
	 * waypoint
	 *
	 * @param a another waypoint instance
	 */
	public void setHeuristic(WayPoint a)
	{
		heuristic = getDistance(a);
	}

	/**
	 *
	 * @param a a waypoint that is used to calculate the distance between the
	 * current waypoint and waypoint a
	 * @return a double value equivalent to the distance between the current
	 * waypoint and waypoint a
	 */
	public double getDistance(WayPoint a)
	{
		return sqrt((x - a.x) * (x - a.x) + (y - a.y) * (y - a.y));
	}

	/**
	 *
	 * @return returns the absolute distance from this waypoint to the
	 * destination
	 */
	public double getHeuristic()
	{
		return heuristic;
	}

	/**
	 * variable used in A* algorithm that keeps track of the total distance that
	 * the bot has traversed up until this point
	 *
	 * @param a WayPoint used to determine the distance from the current
	 * waypoint to waypoint a
	 */
	public void setCurrentCost(WayPoint a)
	{
		currentCost = a.getCurrentCost() + getDistance(a);
		setValue();
	}

	/**
	 *
	 * @return the cumulative traversal cost of this waypoint's parent plus the
	 * cost to get from that parent to this waypoint
	 */
	public double getCurrentCost()
	{
		return currentCost;
	}

	/**
	 * Returns the distance from the current waypoint to one of it's neighbors
	 * denoted by index i
	 *
	 * @param i index of the neighbor who's distance you want to retrieve
	 * @return double value of the distance between current waypoint and it's
	 * i-th neighbor
	 */
	public double getDistances(int i)
	{
		return distances[i];
	}

	/**
	 * Gets called if a player has picked up this waypoint's map is used as a
	 * flag to tell if this waypoint's map has already been used so that the
	 * player does not get stuck in an endless loop
	 */
	public void setVisitedMap()
	{
		visitedMap = true;
	}

	/**
	 * @return returns the status of the map control flag, see setVisitedMap()
	 * for more details
	 */
	public boolean getVisitedMap()
	{
		return visitedMap;
	}

	/**
	 * This is called when a player traverses a waypoint that has gold. It is
	 * used to keep track of where the player has been
	 */
	public void setGoldGone()
	{
		goldGone = true;
	}

	/**
	 * @return Once a map has been collected, this method is called to find out
	 * if the player has already collected the gold that the map linked to
	 */
	public boolean getGoldGone()
	{
		return goldGone;
	}

	/**
	 * @return a string formated in such a way that it can be used as a hashmap
	 * key
	 */
	public String getMapXY()
	{
		return mapX + " " + mapY;
	}

	/**
	 * Sets the value that the priority queue utilizes for the comapareTo method
	 */
	public void setValue()
	{
		value = currentCost + heuristic;
	}

	/**
	 *
	 * @return the value that the compareTo method utilizes for sorting the
	 * priority queue
	 */
	public double getValue()
	{
		return value;
	}
}
