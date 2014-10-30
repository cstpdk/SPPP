SHELL := bash
JAVA := java -cp `pwd`:`pwd`/pcpp-week07

SOURCES := $(shell find . -name "*.java")

CLASSES := $(SOURCES:.java=.class)

.PHONY: all-classes

default: run-7.1

sources.txt:
	echo $(SOURCES) > sources.txt

%.class: $(SOURCES)
	cd `dirname $@` && javac `basename $(@:.class=.java)`

all-classes: sources.txt
	javac @sources.txt

run-7.1: pcpp-week07/IntToDouble.class pcpp-week07/Timer.class pcpp-week07/TestStripedMap.class
	(cd pcpp-week07 && $(JAVA) -ea TestStripedMap)

clean:
	rm -f *.class

run-8:
	cd pcpp-week08 && java TestFetchWebGui
