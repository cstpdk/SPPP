exercises := $(wildcard ex*)

.PHONY: $(exercises)

default: exam

build:
	docker build -t sppp .

$(exercises):
	docker run -u 1000 -it -v $(shell pwd)/$@:/app --entrypoint make sppp default
