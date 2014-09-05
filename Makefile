exercises := $(wildcard ex*)

.PHONY: $(exercises)

default: ex2

build:
	docker build -t sppp .

$(exercises):
	docker run -it -v $(shell pwd)/$@:/app --entrypoint make sppp default
