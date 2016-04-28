rem   This is a batch file that you can run from the command line to
rem   compile the Comp 282 simulation framework and 
rem   make all the on-line javadoc documentation files.
rem   The documentation will be placed in the Doc subdirectory.
rem   The ^ character allows for multi-line batch file commands.
rem
rem   Mike Barnes 8/15/2015

rem   To compile at the command line without setting environment variables
rem   remove the "rem" and edit the line below to set CLASSPATH for the
rem   current command shell.
rem   You would replace everything after "%CLASSPATH%;" with your path.

rem   set CLASSPATH=%CLASSPATH%;C:\Users\renzo\Documents\Csun\cs282\282F15\282projects\SimulationFrameworkV4;


rem   Delete all class files and documentation files.
del *.class Doc /q

rem   Compile the *.java files in this order.
javac Drawable.java
javac Marker.java
javac Connector.java
javac Bot.java
javac AnimatePanel.java
javac SimFrame.java

rem   Make the documentation.

javadoc -author -version -private -overview ./UML/overview.html ^
 -link http://docs.oracle.com/javase/8/docs/api/ ^
 -sourcepath ./;./UML ^
 -d Doc ^
 *.java