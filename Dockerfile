FROM openjdk:8-jdk

MAINTAINER Falko Gloeckler "falko.gloeckler@mfn-berlin.de"

RUN	sed -i 's/# \(.*multiverse$\)/\1/g' /etc/apt/sources.list && \
	apt-get update && \
	apt-get -y upgrade && \
	apt-get install -y vim wget curl git maven
	
RUN mkdir -p /usr/local/dina-labels/tmp && \
	mkdir -p /usr/local/dina-labels/public/pdfs && \
	mkdir -p /usr/local/dina-labels-src


# replace the following line by the GitHub checkout
# git clone https://github.com/MfN-Berlin/dina-labels.git
COPY src /usr/local/dina-labels-src/src
COPY pom.xml /usr/local/dina-labels-src/pom.xml
COPY templates /usr/local/dina-labels/templates

RUN cd /usr/local/dina-labels-src && \
	mvn clean install

RUN mv /usr/local/dina-labels-src/target/dina-labels-jar-with-dependencies.jar /usr/local/dina-labels/dina-labels.jar

# Clean up
RUN rm -rf /usr/local/dina-labels-src
RUN apt-get clean && rm -rf /var/lib/apt/lists/*

VOLUME /usr/local/dina-labels/templates
VOLUME /usr/local/dina-labels/tmp
VOLUME /usr/local/dina-labels/public
	
EXPOSE 80

WORKDIR "/usr/local/dina-labels/"
CMD ["java", "-cp", "/usr/local/dina-labels/dina-labels.jar", "labels.Main"]
