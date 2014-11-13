SHELL := bash
JAVA := java -cp multiverse-core-0.7.0.jar:. -ea

SOURCES := $(shell find . -name "*.java")

CLASSES := $(SOURCES:.java=.class)

.PHONY: all-classes

default: run-10.2

%.class: $(SOURCES)
	javac -cp multiverse-core-0.7.0.jar:. $(@:.class=.java)

run-10.2: TestStmHistogram.class
	$(JAVA) TestStmHistogram

run-10.3: Timer.class TestStmMap.class
	$(JAVA) TestStmMap

clean:
	rm *.class
