SHELL := bash
JAVA := java -ea

SOURCES := $(shell find . -name "*.java")

CLASSES := $(SOURCES:.java=.class)

default: run-Quicksorts

%.class: %.java
	javac $(@:.class=.java)

run-Quicksorts: Quicksorts.class
	$(JAVA) Quicksorts

clean:
	rm *.class
