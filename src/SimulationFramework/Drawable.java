/*
Drawable.java

Mike Barnes
8/12/2013
*/

package SimulationFramework;
// CLASSPATH = ... /282projects/SimulationFrameworkV3
// PATH = ... /282projects/SimulationFrameworkV3/SimulationFramework

import java.awt.*;

/**
Drawable interfaces must implement public Draw(Graphics G).
In the Simulation Framework Markers, Connectors, and Bots are Drawable.
Interface type allows all drawables to be held in same collection.


@since 8/12/2013
@version 3.0
@author G. M. Barnes
*/

public interface Drawable {
	public void draw(Graphics g);
	}