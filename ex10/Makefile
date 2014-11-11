SHELL := bash
JAVA := java -cp `pwd`

SOURCES := $(shell find . -name "*.java")

CLASSES := $(SOURCES:.java=.class)

.PHONY: all-classes

default: run-10.1

%.class: $(SOURCES)
	javac $(@:.class=.java)

run-10.2: TestStmHistogram.class
	$(JAVA) -ea TestStmHistogram

clean:
	rm -f *.class
