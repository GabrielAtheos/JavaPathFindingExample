# Linux or Mac OSX makefile for Comp 282 simulation framework distribution.
# All files in SimulationFramework
# Mike Barnes 9/4/2015

FRAMEWORK=/home/gabriel/Documents/CSUN/cs282/projects/SimulationFrameworkV4

all:	

	rm -rf *.class Doc

	javac -cp ${FRAMEWORK}:${CLASSPATH} Drawable.java
	javac -cp ${FRAMEWORK}:${CLASSPATH} Marker.java
	javac -cp ${FRAMEWORK}:${CLASSPATH} Connector.java
	javac -cp ${FRAMEWORK}:${CLASSPATH} Bot.java
	javac -cp ${FRAMEWORK}:${CLASSPATH} AnimatePanel.java
	javac -cp ${FRAMEWORK}:${CLASSPATH} SimFrame.java

	javadoc -author -version -private -overview UML/overview.html -link http://docs.oracle.com/javase/8/docs/api/ -sourcepath ../:../UML -d Doc *.java
