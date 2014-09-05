FROM java:8

RUN apt-get update
RUN apt-get install make

ADD . /app
WORKDIR /app

ENTRYPOINT ["java","-cp","/app"]
