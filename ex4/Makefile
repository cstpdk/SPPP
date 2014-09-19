SHELL := bash
JAVA := java -cp `pwd`

SOURCES := $(shell find . -name "*.java") #$(wildcard *.java)

CLASSES := $(SOURCES:.java=.class)

marks41 := $(shell echo Mark{0..7})
mark41results := $(marks41:%=%.result)

.PHONY: .run-* print-results

default: run-4.2

%.class: $(SOURCES)
	javac $(@:.class=.java)

cpuinfo.txt:
	echo -e "Cores\t\t: `cat /proc/cpuinfo | grep -E "model name" | wc -l`" > $@
	cat /proc/cpuinfo | grep -E "model name|cache size" | head -2 >> $@

%.result:
	$(JAVA) Benchmark $(patsubst %.result,%,$@) | tee $@

run-4.1: Benchmark.class $(mark41results)

run-4.2: TestTimeThreads.class
	#$(JAVA) TestTimeThreads | tee 4.2.mark6.result
	$(JAVA) TestTimeThreads | tee 4.2.mark7.result

answers.pdf: answers.md
	pandoc -o answers.pdf answers.md

print-results:
	find . -name "Mark*.result" -exec echo {}  \; -exec cat {} \; | grep -v "#"

clean:
	rm -f *.class
	rm *.result
