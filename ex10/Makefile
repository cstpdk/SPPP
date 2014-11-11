SHELL := bash
JAVA := java -cp `pwd`

SOURCES := $(shell find . -name "*.java")

CLASSES := $(SOURCES:.java=.class)

.PHONY: all-classes

default: run-10.1

%.class: $(SOURCES)
	$ javac -cp ~/lib/multiverse-core-0.7.0.jar stm/TestStmHistogram.java

run-10.2: TestStmHistogram.class
	$ java -cp ~/lib/multiverse-core-0.7.0.jar:. stm/TestStmHistogram

clean:
	rm -f *.class
