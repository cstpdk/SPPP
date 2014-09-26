.PHONY: .run-*
JAVA := java -cp `pwd`

SOURCES := $(wildcard *.java)

CLASSES := $(SOURCES:.java=.class)

default: run-2.3

%.class: $(SOURCES)
	javac $(@:.class=.java)

run-2.1: TestMutableInteger.class
	$(JAVA) TestMutableInteger

run-2.2: TestCountPrimes.class
	$(JAVA) TestCountPrimes

run-2.3: TestCountFactors.class
	$(JAVA) TestCountFactors

run-2.4: TestFactorizer.class
	$(JAVA) TestFactorizer

clean:
	rm -f *.class
