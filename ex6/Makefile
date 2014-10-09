SHELL := bash
JAVA := java -cp `pwd`

SOURCES := $(shell find . -name "*.java") #$(wildcard *.java)

CLASSES := $(SOURCES:.java=.class)

.PHONY: .run-* print-results

default: run-6.1

%.class: $(SOURCES)
	javac $(@:.class=.java)

run-6.1: src/TestAccountDeadlock.class
	(cd src && java TestAccountDeadlock)

run-6.2: src/TestAccountLockOrder.class
	(cd src && java TestAccountLockOrder)

clean:
	rm -f *.class
