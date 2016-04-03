# LogStorm Design

## Terms
* __Pipeline__: application pipeline definition
* __Application__: pipeline + execution application (storm topology, spark streaming application or flink streaming application, etc.)
* __Plugin__: logstash native plugins which will used executed in distributed way with logstorm

## Principle
* Provide tools for different layers of users: non-programming users / developers / contributors
* Use embedded web server instead of being hosted inside external web servers

## Technical Stack
* Java Version: JDK 8
* LogStash: 2.2.0
* Web Server: Use embedded tomcat/jetty from starting instead of building as war.
* Web Framework: Spring boot (version: 1.3.3-RELEASE)
* Front-end Framework: Bootstrap Theme and AdminLTE

## Target Customers
* Simply Submit logstash configuration directly as distributed streaming application on storm/spark/flink, etc.
* Simply Submit logstash configuration directly on web ui
* Program logstash-based distrbuted application with Java API
* Use LogStash as Native API/Library using Java interface.
* Pipeline  lifecyle(start/stop/status/rescale) management on web ui

## Command-Line Design

`logstorm-submit`

## Web UI Design
* Submit: pipeline content + selected cluster
* Pipeline (Lifecyle Management): define pipeline + deploy execution + status checking
* Environment: environment name, type, properties, master
* Configuration: Server configuration

## Streaming Platform Adapter Interface
* Runner (for simply submitting with shell or web ui)
* Platform (for rich pipeline  lifecyle(start/stop/status/rescale) management on web ui)

## REST API Design

### Security/Authentication
> Basic Authentication: https://en.wikipedia.org/wiki/Basic_access_authentication
	
	Authorization: Basic QWxhZGRpbjpPcGVuU2VzYW1l
	
### Request/Response Structure
* Request:
	
	    {
		    ...
	    }

* Response:
	
	    {
		    "success":true,
		    "message": null,
		    "url":"http://../api/path/"
	    }

### Pipeline Metadata API
* `GET /api/pipeline`: list all pipelines
* `GET /api/pipeline/{id}`: get pipeline detail information
* `POST /api/pipeline`: create new pipeline 
* `PUT /api/pipeline/{id}`: update pipeline
* `DELETE /api/pipeline/{id}`: delete pipeline

### Pipeline Operation API
* `GET /api/pipeline/{id}/status`
* `POST /api/pipeline/{id}/start`
* `POST /api/pipeline/{id}/stop`
* `POST /api/pipeline/{id}/rescale`

### Plugin Metadata API (Optional)
* `GET /api/plugin/input`: list all input plugins
* `GET /api/plugin/filter`: list all filter plugins
* `GET /api/plugin/output`: list all output plugins
