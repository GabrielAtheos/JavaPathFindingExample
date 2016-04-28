/*
Connector.java

Mike Barnes
1/26/2014
*/

package SimulationFramework;
// CLASSPATH = ... /282projects/SimulationFrameworkV4
// PATH = ... /282projects/SimulationFrameworkV4/SimulationFramework

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
Connector is used to draw lines between points on the simulation's
canvas.  Often Connectors are used to join 2 Markers.  In algorithm
simulation Connectors can be either temporary or permanent.  Permanent
connectors are only cleared when the simulation's "Reset" option is
selected.  Temporary connectors are cleared when the simulation's
"Clear" option is selected.

@since 1/26/2014
@version 3.3
@author G. M. Barnes
*/

public class Connector implements Drawable {
	/** connector's beginning position */
	private Point pointA;
	/** connector's beginning position */
	private Point pointB;
	/** connector's color */
	private Color color;

/**
Make a Connector using screen positions
@param x connector's beginning horizontal screen position 
@param y connector's beginning vertical screen  position
@param x2 connector's end horizontal screen position 
@param y2 connector's end vertical screen  position
@param colorValue connector's color
*/

	public Connector(int x, int y, int x2, int y2, Color colorValue) {
		pointA = new Point(x, y);
		pointB = new Point(x2, y2);
		color = colorValue;
		}

/**
Make a Connector using 2 markers
@param ptA connector begins at this Point
@param ptB connector ends at this Point
@param colorValue connector's color
*/

	public Connector(Point ptA, Point ptB, Color colorValue) {
		pointA = ptA;
		pointB = ptB;
		color = colorValue;
		}

/**
Make a Connector using 2 markers
@param m1 connector begins at this Marker
@param m2 connector ends at this Marker
@param colorValue connector's color
*/

	public Connector(Marker m1, Marker m2, Color colorValue) {
		pointA = m1.getPoint();
		pointB = m2.getPoint();
		color = colorValue;
		}

/** Draw the connector on the simulation's canvas */

  public synchronized void draw(Graphics g) {
		Color tColor;
		tColor = g.getColor();  // save exisiting color
		g.setColor(color);
		g.drawLine((int) pointA.getX(), (int) pointA.getY(),
			(int) pointB.getX(), (int) pointB.getY());
		g.setColor(tColor);   // restore previous color
		}

/** return Connector's pointA endpoint */
  public Point getPointA() { return pointA; }

/** return Connector's pointB endpoint */
  public Point getPointB() { return pointB; }
   
/**
Test if 2 connectors have the same end points,
regardless of order.
*/
  public boolean equals (Connector other) {
    if (pointA.equals(other.getPointA()) &&
      pointB.equals(other.getPointB()) ) return true;
    else if (pointA.equals(other.getPointB()) &&
      pointB.equals(other.getPointA()) ) return true;
    else return false;
    }

	}