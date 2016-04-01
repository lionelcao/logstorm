# LogStorm Design


## REST API Design

### Authentication
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
* `POST /api/pipeline/{id}/scale`
* `POST /api/pipeline/{id}/suspend`

### Plugin Metadata API (Optional)
* `GET /api/plugin/input`: list all input plugins
* `GET /api/plugin/filter`: list all filter plugins
* `GET /api/plugin/output`: list all output plugins
