SHELL := bash
JAVA := java -cp `pwd`

SOURCES := $(shell find . -name "*.java") #$(wildcard *.java)

CLASSES := $(SOURCES:.java=.class)

.PHONY: .run-* print-results

default: MathFunctionBenchmarks.result

%.class: $(SOURCES)
	javac $(@:.class=.java)

clean:
	rm -f *.class
