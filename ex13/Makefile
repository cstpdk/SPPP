SHELL := bash

JAVA := java -cp scala.jar:akka-actor.jar:akka-config.jar:.

JAVAC := javac -cp scala.jar:akka-actor.jar

SOURCES := $(shell find . -name "*.java")

CLASSES := $(SOURCES:.java=.class)

default: run-java

%.class: %.java
	$(JAVAC) $(@:.class=.java)

run-java: $(CLASSES)
	$(JAVA) ABC

clean:
	rm *.class
