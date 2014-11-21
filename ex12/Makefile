SHELL := bash
JAVA := java -ea

SOURCES := $(shell find . -name "*.java")

CLASSES := $(SOURCES:.java=.class)

default: run-12.1

%.class: %.java
	javac $(@:.class=.java)

run-12.1:
	$(JAVA)

clean:
	rm *.class
