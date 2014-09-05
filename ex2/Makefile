
JAVA := java -cp `pwd`

SOURCES := $(wildcard *.java)
CLASSES := $(SOURCES:.java=.class)

default: run

%.class:
	javac $(@:.class=.java)

run: $(CLASSES)
	$(JAVA) TestMutableInteger

clean:
	rm *.class
