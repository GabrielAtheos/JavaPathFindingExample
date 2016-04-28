/*
SimFrame.java

Mike Barnes
8/14/2015
*/

package SimulationFramework;
// CLASSPATH = ... /282projects/SimulationFrameworkV4
// PATH = ... /282projects/SimulationFrameworkV4/SimulationFramework

import java.awt.*;
import java.awt.event.*;  
import javax.swing.*;
import javax.swing.event.*; 

/**
An abstract class for the framework's GUI.  SimFrame
declares all GUI components and containers used by its subclass.
SimFrame instantiates an AnimatePanel to display the simulation.
SimFrame instantiates all the "state control" buttons, defines their
actionPerformed methods.  SimFrame is the "glue" that enables
simulation of algorithms to be started, stopped, cleared, and reset.
It declares the abstract methods that it sub-class will have to
override.  SimFrame should be sub-classed to define the simulation
"main" class that will setup the simulation model and begin its
animation.


@since 8/14/2015
@version 4.0
@author G. M. Barnes
*/

@SuppressWarnings("serial")  // suppress serial warnings ... JGrasp

public abstract class SimFrame extends JFrame {
  // eliminate warnings @ serialVersionUID
  private static final long serialVersionUID = 42L;
  /** horizontal extent of the simulation space */
  protected final int xDim = 525;
  /** vertical extent of the simulation space */
  protected final int yDim = 610;
  // GUI components
  protected JMenuBar menuBar;
  protected JMenu aboutMenu;
  /** describes how to run / use the program */
  protected JMenuItem usageItem;
  /** information about the model's (program's) designer/developer */
  protected JMenuItem authorItem;
  /** information displayed at the bottom of the frame */
  protected JLabel status;
  /** GUI container for animation of model */
  protected AnimatePanel animatePanel;
  // other variables
  /** true if simulation is running, paused model can start back up */
  private boolean simRunning;
  /** true if model has been setup -- simulation can be "re-run" */
  private boolean simValid;
  /** true iff mousePosition() has been called, waiting for mouse click" */
  private boolean waitingForMouseClick;
  /** where the user clicked the mouse in AnimatePanel.canvas */
  public Point mousePosition;
  /** Used to time algorithm performance at millisecond level
  <br>
  see getAlgoTime() */
  protected long startTime, totalTime;  
   
/**
Make the Frame
@param frameTitle application's window title
@param imageFile background image for AnimatePanel.canvas
*/

  public SimFrame (String frameTitle, String imageFile) { 
    super(frameTitle);   // call base constructor to set title
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout(5,5));
    // add AnimatePanel
    animatePanel = new AnimatePanel(this, imageFile);
    contentPane.add(animatePanel, BorderLayout.CENTER);
    status =   new JLabel("click start button to begin animation");
    status.setToolTipText("display status information to user");
    contentPane.add(status, BorderLayout.SOUTH);
    setResizable(false);
    setBounds(20,20,xDim,yDim);  // set location and size
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);  // display the JFrame
    // create non GUI components
    startTime = totalTime = 0L;
    simRunning = false;
    simValid = false;
    waitingForMouseClick = false;
    mousePosition = null;
    }

/** display information at frame's "statusbar" */
  public void setStatus(String message) {
    status.setText(message);
    repaint(); }

/**
Display message in the status field and print in "console" window.
@param message text to display and print
*/
      
  public void statusReport(String message) {
    setStatus(message);
    System.out.println(message);
    }

/** 
Pause the current thread until the user clicks the mouse in the
AnimatePanel.canvas.  Method setPoint(Point) resumes the thread after 
the mouse click.
*/

  public synchronized void waitForMousePosition() {
    mousePosition = null;
    waitingForMouseClick = true;
    try { 
      while (mousePosition == null) wait(); }
    catch (InterruptedException ie) { 
      System.out.println("Error in wait() " + ie.toString()); }
    }

/** 
Get the mouse position and resume waiting thread.
Method waitForMousePOsition() paused the thread to wait for the position.
@param point where the mouse was clicked in AnimatePanel.canvas
*/

  protected synchronized void setPoint(Point point) {
    if (waitingForMouseClick) {
      mousePosition = point; 
      waitingForMouseClick = false;
      notifyAll(); }
    }

/**
When simulation model is valid (simValid is true)  repaint() the display 
to show the current simulation state and "step" the simulation by having
the simulation thread wait AnimatePanel.delayInterval milliseconds.
When the simulation has been "reset" (simValid is false) wait
10 milliseconds and before allowing user to setup a new simulation model
*/

  private synchronized void delay() {
    animatePanel.repaint();
    try { 
      if (!simValid) // restart, get new model 
      wait(10); 
      else          //  pause animation
      wait(animatePanel.getDelayInterval()); 
      }
    catch (InterruptedException ie) { 
      System.out.println("Error in delay() " + ie.toString()); }
    }
   

/** 
When simulation model has been reset (simValid is false) there are
two wait stages for the simulation thread pass.  First is the wait for
the simulateAlgorith() and second is for start()
*/

  protected synchronized void stopWait() {
    notifyAll();
    if (!simValid) {
      delay();
      notifyAll();
      }
    }
		
/** Used to see if simulation can be run 
@return true if model can be simulated
*/		
	protected boolean runnable() {
		return simRunning;
		}

/** Used to query the simRunning state value 
@return true if simulation is running
*/
  protected boolean isSimRunning() { 
    return simRunning; }

/** Used to query the model's valid (simValid) state value 
@return true when there is a valid simulation model
*/
  protected boolean isModelValid() { 
    return simValid; }
      
/** Used to set the simRunning state variable */
  protected void setSimRunning (boolean value) {
    simRunning = value; 
    }

/** Used to set the simValid state variable */
  protected void setModelValid(boolean value) {
    simValid = value; }

/** 
Used to get time for the start or end an algorithm execution in nanoseconds. 
<p>
To accumulate total algorithm execution across several "steps" inside
simulateAlgorithm()'s animation, do something like:
<pre>
     startTime = getAlgoTime()
     ... // algorithm executes
     ... // usually inside simulateAlgorithm() 
     totalTime += (getAlgoTime() - startTime();
</pre>
@return current system time in nanoseconds
*/
  public long getAlgoTime() {
    return System.nanoTime(); 
    }
      
/** 
Get a reference to the AnimatePanel
@return the AnimationPanel
*/  
    
  public AnimatePanel getAnimatePanel() { return animatePanel; }     
   
/** Convert totalTime from nanoseconds to milliseconds 
@return the time of the simulation in nanoseconds
*/
  public double timeInMilliseconds() {
    return totalTime / 1000000.0; }
      
/**
The simulation's main class must override this method to 
set up the simulation model's initial state.
<p>
<pre>
// Below is a skeleton of the code necessary for
// all overridden abstract setSimModel() method.

	protected void setSimModel() {
		// Declare and set any local control variables.
		// Or set up the initial algorithm state:
		}
</pre>    
*/

  protected abstract void setSimModel();

/**
This method is a "callback" from AnimatePanel so the application can
stop/start, clear, and reset the execution of an algorithm (like: path
finding).  Look at how DemoSimFrame overrides.
<p>
<pre>
// Below is a skeleton of the code necessary for
// all overridden abstract simulateAlgorithm() method.
   
   protected void simulateAlgorithm()  {
		// Declare and set any local control variables.
		// Or set up the initial algorithm state:
		// declare and set any algorithm specific varibles
      while (simRunning) {
         // 
         // In overridden abstract simulateAlgorith()
         // put your code here.
         //
         // the following statement must be at end of any
         // overridden abstract simulateAlgorithm() method.
         checkStateToWait();
         }
      }
</pre>
*/

  protected abstract void simulateAlgorithm();


/**
Determine how many times thread needs to wait based on state of
simulation model.
*/

  public void checkStateToWait() {
    delay();  // will cause a repaint().
    if (!simValid) 
        return;
     else {
      try {   if(!simRunning) wait(); }
      catch (InterruptedException ie) { 
        System.out.println("Error in simulation wait() " 
        + ie.toString()); }
      }
  }

/**
Facilitate settimg up the model in the simulation's main class (a
subclass of SimFrame). Also "hides" the wait / notify interaction needed
for thread control in this framework.
*/

  protected synchronized void start() {
    do {
      // Beta 3.1 SimFrame change uses the following if statement
      // prior to the try wait()
      // Otherwise setPoint will resume this thread w/ mouse clicks.
      if (! simValid && ! simRunning) {
        setSimModel();
        // enable animatePanel's UI controls
        animatePanel.setComponentState(false, true, true, false, false);
        setStatus("simulation running:  animation delay is 1000 msec.");
        // set simulated state variables true
        simValid = true;
        simRunning = true;
        simulateAlgorithm(); 
        }
      // wait for simulation to be restarted -- keep thread alive
      try { wait(); }
      catch (InterruptedException ie) { 
        System.out.println("Error in wait() " + ie.toString()); }
      } while (true);
    }

  }