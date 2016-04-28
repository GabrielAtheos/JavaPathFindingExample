/*Player
 *Gabriel Sturtevant
 *11/08/2015
 */

import SimulationFramework.*;
import java.awt.Color;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author gabriel
 */
public class Player extends Bot
{

	private double strength = 2000;
	private int wealth = 1000;

	/**
	 * Player is an extension of the Bot class in SimulationFramework The
	 * constructor receives arguments consisting of a String name, x and y
	 * position integers and a color value then calls the super class in Bot to
	 * construct them Player also keeps track of the player's wealth and
	 * strength
	 *
	 * @param name
	 * @param x
	 * @param y
	 * @param colorValue
	 */
	public Player(String name, int x, int y, Color colorValue)
	{
		super(name, x, y, colorValue);
	}

	/**
	 * Subtracts foo from the current strength
	 *
	 * @param foo
	 */
	public void subtractStrength(double foo)
	{
		strength -= foo;
	}

	/**
	 * adds foo to the current strength
	 *
	 * @param foo
	 */
	public void addStrength(int foo)
	{
		strength += foo;
	}
	/**
	 * Sets player's strength to zero
	 */
	public void zeroStrength()
	{
		strength = 0;
	}

	/**
	 * Returns the current strength value
	 *
	 * @return double: current strength value
	 */
	public double getStrength()
	{
		return strength;
	}

	/**
	 * Adds foo to the current wealth
	 *
	 * @param foo
	 */
	public void addWealth(int foo)
	{
		wealth += foo;
	}

	/**
	 * Subtracts foo from the current wealth
	 *
	 * @param foo
	 */
	public void subtractWealth(int foo)
	{
		wealth -= foo;
	}

	/**
	 * Returns the current wealth
	 *
	 * @return int: current wealth
	 */
	public int getWealth()
	{
		return wealth;
	}
}
