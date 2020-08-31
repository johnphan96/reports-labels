# DINA Reports & Labels Service

This REST web-service makes use of [Twig templates](https://twig.symfony.com) and can be fed with arbitrary JSON objects in order to create printable labels in HTML or PDF format.


Installation
===
* install [Docker](https://docs.docker.com/get-docker/)
* install [Docker-Compose](https://docs.docker.com/compose/install/)
* clone this Git repo on your server `git clone <link to repo>`
* open the new directory `cd /path/to/cloned/repo`
* copy or rename `config.ini.example` to `config.ini`
* edit individual configuration according to your server in `config.ini`
* optionally change the exposed port 7981 in `docker-compose.yml` if needed 
* run `docker-compose up -d` for building the Docker image and running the Docker container
* the service should be available at `http(s)://your.domain.org:port/`  e.g. http://localhost:7981/


How to use
===
_(coming soon)_	


 