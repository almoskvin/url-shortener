# REST API implementation for short URL creation

This implementation generates short URLs for an instance URL specified in `instance.url` application property.
It uses an ID provided by MongoDB to generate a short hash string via Google Guava `Hashing.murmur3_32()` method.

## Configuration

This project uses MongoDB as a data storage, so an instance of MongoDB server must be specified in application properties.<br/>
Default values are:
    
    #mongodb
    spring.data.mongodb.host=localhost
    spring.data.mongodb.port=27017
    spring.data.mongodb.database=URL_SHORTENER_DB

If there is no MongoDB instance available, the Docker image can be pulled and run with the following simple commands:
    
    docker pull mongo && \
    docker run --rm --user mongodb -p 27017:27017 --name url-shortener-mongo mongo    

URL of a current application instance must be specified as well (`http://localhost:8080/` by default):

    #instance
    instance.url=http://localhost:8080/

For example, if the application runs with a property `instance.url=https://your.in/`, then it will generate short URLs like this `https://your.in/3h4jhg5k`

## Running
### Run app as a separate container
Presentation image of this project (generates short links for `http://localhost:8080/`) is available on DockerHub <https://cloud.docker.com/repository/docker/esquire/url-shortener/>.<br>
This image must be run only linked with a MongoDB container.

Following commands are sufficient for running the project successfully:

    docker pull mongo && \
    docker run --rm --user mongodb -p 27017:27017 --name url-shortener-mongo mongo    

    docker pull esquire/url-shortener && \
    docker run --rm -p 8080:8080 --name url-shortener --link=url-shortener-mongo esquire/url-shortener

It is important to mention that the published image is not ready for a production environment. For production level, there are things which need to be configured thoroughly.
### Run app using docker-compose
It is possible to use provided `docker-compose.yml` file by running the command below in the root folder of the project:

    docker-compose up --build

## Actions
### Create
**POST request, param `link` is required**

To shorten the URL <https://duckduckgo.com/?q=anchorage+daily+news>, send the following request:

    POST %instance.url%/api/v1/urlLinker
    Content-Type: application/json
    
    {"link": "https://duckduckgo.com/?q=anchorage+daily+news"}

For example, you can use the following `curl` command for a default instance URL:

    curl http://localhost:8080/api/v1/urlLinker \
        -H 'Content-Type: application/json' \
        -d '{"link": "https://duckduckgo.com/?q=anchorage+daily+news"}'

If successful, the response will look like:

    {
     "alias": "http://localhost:8080/g43g2hg3",
     "link": "https://duckduckgo.com/?q=anchorage+daily+news"
    }

If URL in the `link` param is invalid, the response will be `HTTP 400 (Bad request)`.        
        
### Redirect

**GET request**

If you have a short URL, just follow the link in a web-browser, or you can use a simple `curl` command:

    curl %short_URL%

For example, the following `curl` command could be used for a default instance URL:

    curl http://localhost:8080/3h4jhg5k

Response will be `HTTP 404` in case the original link for this alias is not found.

### Expand

**GET request, param `alias` is required, param `projection` is optional**

You can call this method if you want to retrieve information about some short link.<br>
Just send the following request:

    GET %instance.url%/api/v1/urlLinker?alias=%short_URL%
    
For example, the following `curl` command could be used for a default instance URL to retrieve information about URL <http://localhost:8080/358ee832>:    

    curl http://localhost:8080/api/v1/urlLinker?alias=http://localhost:8080/358ee832

If successful, the response will look like:

    {
     "alias":"http://localhost:8080/358ee832",
     "link":"https://duckduckgo.com/?q=anchorage+daily+news"
    }
    
Also, it is possible to set an optional param `projection` to `FULL` to get additional information:

    GET %instance.url%/api/v1/urlLinker?alias=%short_URL%&projection=FULL

`curl` command from the last example could look like this:

    curl 'http://localhost:8080/api/v1/urlLinker?alias=http://localhost:8080/358ee832&projection=FULL'

If successful, the response will look like:

    {
     "alias":"http://localhost:8080/358ee832",
     "analytics":
     {
        "created":"2019-03-04T12:39:52.878+0000",
        "followedTimes":0,
        "lastTimeFollowed":null
     },
     "link":"https://duckduckgo.com/?q=anchorage+daily+news"
    }
    
In the other case, response will be `HTTP 404`.         

### Delete

**DELETE request, param `alias` is required**

A short URL record can be deleted by sending the following request:

    DELETE %instance.url%/api/v1/urlLinker?alias=%short_URL%
    
For example, the following `curl` command could be used for a default instance URL to delete information about URL <http://localhost:8080/358ee832>::

      curl -X DELETE http://localhost:8080/api/v1/urlLinker?alias=http://localhost:8080/358ee832

If successful, the response will be `HTTP 200`, or `HTTP 404` if the record does not exist.
        
