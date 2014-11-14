SHELL := bash
JAVA := java -ea

SOURCES := $(shell find . -name "*.java")

CLASSES := $(SOURCES:.java=.class)

default: run-11.1

%.class: $(SOURCES)
	javac $(@:.class=.java)

run-11.1: $(CLASSES)
	$(JAVA) TestCasLocks

clean:
	rm *.class
