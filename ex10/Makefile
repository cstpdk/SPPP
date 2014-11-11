SHELL := bash
JAVA := java -cp `pwd`

SOURCES := $(shell find . -name "*.java")

CLASSES := $(SOURCES:.java=.class)

.PHONY: all-classes

default: run-10.3

%.class: $(SOURCES)
	javac $(@:.class=.java)

run-10.2: $(CLASSES)
	$(JAVA) -ea TestStmHistogram

run-10.3: $(CLASSES)
	$(JAVA) -ea TestStmMap

clean:
	rm *.class
