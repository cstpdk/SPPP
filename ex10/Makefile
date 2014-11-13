SHELL := bash
JAVA := java -cp multiverse-core-0.7.0.jar:.

SOURCES := $(shell find . -name "*.java")

CLASSES := $(SOURCES:.java=.class)

.PHONY: all-classes

default: run-10.2

%.class: $(SOURCES)
	javac -cp multiverse-core-0.7.0.jar TestStmHistogram.java

run-10.2: TestStmHistogram.class
	$(JAVA) TestStmHistogram

run-10.3: $(CLASSES)
	$(JAVA) TestStmMap

clean:
	rm *.class
