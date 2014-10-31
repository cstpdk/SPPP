SHELL := bash
JAVA := java -cp `pwd`

SOURCES := $(shell find . -name "*.java")

CLASSES := $(SOURCES:.java=.class)

.PHONY: all-classes

default: run-9.1

sources.txt:
	echo $(SOURCES) > sources.txt

%.class: $(SOURCES)
	cd `dirname $@` && javac `basename $(@:.class=.java)`

all-classes: sources.txt
	javac @sources.txt

run-9.1: IntToDouble.class Timer.class TestStripedMap.class
	$(JAVA) -ea TestStripedMap

clean:
	rm -f *.class

run-8:
	cd pcpp-week08 && java TestFetchWebGui
