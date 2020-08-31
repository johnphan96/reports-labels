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
The service expects three parameters (POST method)
* ___template___ ... the relative path (incl. file name) to the template that should be used. The path needs to be relative to the template directory.  
e.g.:  `Mammalia.twig` or `./mySubdirectory/myTemplate.twig`
* ___data___ the stringified JSON array that contains the data to be wrapped in the corresponding template.
Be aware that each object name in the JSON Array contains the name of the placeholders in the template.
* ___format___ the format of the output which can be `HTML` or `PDF`. PDFs will be returned as an octet-stream.
 

New templates can be copied and modified the `templates` folder during runtime. So there is no need to restart the service.
For your convenience there is a template called `submit.html` (e.g. http://localhost:7981/labels/v1.0/template/show?template=/submit.html). This can be used to post the parameters without huge effort.


Sample data
=== 
The folders `templates` and `sample_data` in this repo contain two examples:
* ___Schrank_labels___ is a simple label with a QR-code and the full text of the identifier encoded.
* ___Mammalia_labels___ is a complex template with corresponding data for collection objects from the MfN mammals collection.



 