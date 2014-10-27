SHELL := bash
JAVA := java -cp `pwd`

SOURCES := $(shell find . -name "*.java") 

CLASSES := $(SOURCES:.java=.class)

.PHONY: .run-* print-results

default: run-7.1

%.class: $(SOURCES)
	javac $(@:.class=.java)


run-7.1: pcpp-week07/TestStripedMap.class pcpp-week07/IntToDouble.class pcpp-week07/Timer.class
	(cd pcpp-week07 && java TestStripedMap)

clean:
	rm -f *.class
