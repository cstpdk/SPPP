SHELL := bash
JAVA := java -ea

SOURCES := $(shell find . -name "*.java")

CLASSES := $(SOURCES:.java=.class)

default: run-11.2

%.class: %.java
	javac $(@:.class=.java)

run-11.1: TestCasHistogram.class
	$(JAVA) TestCasHistogram

run-11.2: TestCasLocks.class
	$(JAVA) TestCasLocks

clean:
	rm *.class
