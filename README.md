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


Generating Template
===
(_To be completed_)

### Specific Twig Functions

#### generateCode
___@Function___: _`generateCode(data, filename, format, [parameters])`_

___@Description___: _Creates barcodes, QR codes and data matrixes on the fly_<br/>
___@Parameters___:

| Parameter 	| Sub-Parameter			| Description                                                                                                                	| Data Type   	| allowed values                        	| default value               	| optional 	|
|------------	|-------------------	|----------------------------------------------------------------------------------------------------------------------------	|-------------	|---------------------------------------	|-----------------------------	|----------	|
| data       	|                   	| the string to be encoded                                                                                                   	| string      	| any string                            	|                             	| no       	|
| filename   	|                   	| name of the image file for the code                                                                                        	| string      	| any string                            	|                             	| no       	|
| format     	|                   	| set the type of encoding                                                                                                   	| string      	| QR-Code = QR<br>Barcode<br>DataMatrix 	|                             	| no       	|
| parameters 	|                   	| pass parameters directly to the zxing library for encoding                                                                 	| JSON object 	| (specified below)                     	|                             	| yes      	|
|            	| width             	| width of the image in pixels                                                                                               	| integer     	|                                       	| 600                         	| yes      	|
|            	| height            	| heigh of the image in pixels                                                                                               	| integer     	|                                       	| 600                         	| yes      	|
|            	| margin            	| margin added to the edges within the image                                                                                 	| integer     	|                                       	| _(library default)_           	| yes      	|
|            	| qrcode_version    	| version of the QR code  (only applied if format=QR)                                                                        	| integer     	|                                       	| _(library default)_           	| yes      	|
|            	| correction        	| correction level  for the codes (L = ~7%, M = ~15%, Q = ~25%, H = ~30%)	| enum        	| L<br>M <br>Q<br>H                     	| _(library default)_           	| yes      	|
|            	| data_matrix_shape 	| force the shape of the data matrix  (only applied if format=DataMatrix)                                                    	| enum        	| square<br>rectangle<br>none           	| _(depends on width x height)_ 	| yes      	|

___@Return value___: _Returns the filename of the generated (temporary) file to be used in HTML_

___@Example___: 
```
{% set qr_code_file = generateCode('http://example.org/123', 'myExample', 'QR', '{ width: 50, height: 50, margin: 3 }')  %}
{% set datamatrix_file = generateCode('http://example.org/123', 'myExample', 'DataMatrix', '{ data_matrix_shape: \'rectangle\' }')  %}
{% set barcode_file = generateCode('http://example.org/123', 'myExample', 'Barcode')  %}

<img src="{{ qr_code_file }}"><br/>
<img src="{{ datamatrix_file }}"><br/>
<img src="{{ barcode_file }}"><br/>
``` 


 