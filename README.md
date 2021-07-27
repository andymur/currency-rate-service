# Coding Task

Create a service which exposes REST API to get instant currency couple rate and historical currency couple rate.

Instant rate query: ```GET /instant/<CURRENCY_COUPLE>```
Historical rate query: ```GET /historical/<FROM_DATE/<TO_DATE>/<CURRENCY_COUPLE>```

Where CURRENCY_COUPLE is ISO currency couple value.
FROM_DATE and TO_DATE are ISO date values. 

## Overview & Notes on architecture

Application itself is straight.
I was trying not to over-engineer and keep it simple :-)
 
We have two REST endpoints here for the latest (instant) rate 

```GET /instant/<CURRENCY_COUPLE>``` where <CURRENCY_COUPLE> is XBTUSD in our case.

Also, for historical rates

```GET /historical/<FROM_DATE/<TO_DATE>/<CURRENCY_COUPLE>``` where <CURRENCY_COUPLE> is XBTUSD in our case and <FROM_DATE>, <TO_DATE> form date interval.

There is also a scheduled task which constantly checks the currency exchange rate according to configurable schedule.
(Each N seconds but could be easily changed to cron-like expression).

The task itself updates the cache which is read by inner component after endpoints request.
Historical queries also stored into the cache for future usage (following requests).  

### Main layers & components

There are four main layers (not including rest endpoints) with components inside of them.
They are forming dependency tree. One layer has a dependency arrow (see *layers-components-dependency-tree.png*) if inner components of it depend on the components of another layer.

Let's briefly describe layers.

* Cache Layer. The Name speaks for itself. We don't have any persistent layer here just a cache.
* Data Abstraction Layer (DAL). It has components for querying and updating the caches. We separate these two parts on purpose.
The main goal of the layer to decouple storage part (cache in our case) and other dependent layers.
* External Provider Layer. Encapsulates everything what's needed for fetching rates from external world. Depends on DAL to store fetched data.
* Controller Layer. It has rate updater task component (which runs periodically) and main component for providing rates to the REST endpoints.

A couple words about caches. We have two for the latest rates and for the historical data.
The first one has currency couple as a key and rate as a value and probably doesn't need any retention policy.
The second one has compound key consists of date interval and currency couple and list of rates as a value. It probably needs some retention policy for production.  

Also, we have parts. Each part might contain several components and resides in one of the layer.

* Data Query Part. Has everything for querying the caches. Resides in DAL. Separated from Data Update Part. 
* Data Update Part. Has everything for updating the caches. Resides in DAL. Separated from Data Query Part.
* External Data Provider Part. Has everything for fetching rates from external world. Provides special facade for internal rate provider. Depends on DAL.
* Internal Rate Provider Part. Provides all type of rates to the REST endpoints. Depends on external provider facade & DAL.
* Rate Updater Part (Task). Launches rate updater task periodically. The task fetches the latest rate and updates it using DAL.

You can check the more detailed version with components from *components.png*

### Main execution flows

All main flows can be found inside *main-execution-flows.png*. It has only three happy-flows described.
The first two about fetching the latest & historical rates (algorithm is the same) for cases when the cache has the key and when it doesn't have it.
The third flow describes updater task execution flow.

Of course there are might be exceptional cases, but you can check them only through the tests.

## Building & Testing the system

Building process is straightforward, you can get the final jar with a ```mvn clean install``` command.
There are two type of tests set: unit and integration tests.

### Unit tests

Unit tests can be launched using ```mvn clean test```. Basically we're testing the controller layer there w/o REST part.
Since that layer depends on many things and no one depends on it (except for REST) we use a lot of mocking in unit-tests.
NB. Historical rate tests set has not been finished but it is supposed to be quite similar to instant (the latest rate) one.

### Integration tests

Integration tests can be launched using special profile and command ```mvn clean verify -Pintegration```.
In contrast to unit tests they have no mocks at all (we don't count fake external world & stubby caches, see _Room for improvements_ section below).
We're testing our REST part here.

NB. Please make sure that port 8090 is free to use (or change test-configuration.yml)
   
## Running & Using the Service.

There are couple ways to run our service.

### From JAR

A simple one, to build it first (see section above) and run with a command (from base directory).

```java -jar target/currency-rate-service.jar server configuration.yml```

### From Docker

You can use power of docker if you'd like.

```docker run -p 8080:8080 currency-rate-service:latest``` or ```run-docker-image.sh```

(please build the image with ```docker build --tag=currency-rate-service:latest .``` or ```build-docker-image.sh``` before the first run)

NB. Please make sure that port 8080 is free to use.

### Using it

As it was said in overview section, we have two endpoints, we can use it.
Example requests (only happy path, but you can try to break it with your own requests of course):

* Get the latest rate: ```curl --header "Content-Type: application/json" "http://localhost:8080/instant/XBTUSD/"```
* Get the historical rate: ```curl --header "Content-Type: application/json" "http://localhost:8080/historical/2020-01-10/2020-01-23/XBTUSD/"```

NB. As described below our external components are crazy, they are stubs. The latest would return some random value and the historical list of randoms (in some interval).

## Room for improvements.

For sure it is not production-ready (unfortunately), but I'm already a bit out of time :-)

##### No schema for REST API.

I could use ```https://swagger.io/``` to design & document it.

##### External components are fake. :-)

Crazy external world. Some real external API could be used.

##### Cache implementation is fake. :-)

The caches are in jvm memory maps. I could use more production-ready cache storage (e.g. Redis).

##### Rates facade should be better configurable with external components.

Highly likely we will extend external provider facade with more external components. Now it is not super configurable. 

##### Currency couple list is not configurable with rate updater task.

Same here, for now updater task uses hard coded currency couples for updating their rates. Could be more flexible.

##### No health checks provided.

We need them for production-ready solution.

##### Non covered tests scenarios.

I can imagine test case when our cached couldn't work at all on any request to it. This would end with 500 code and not covered.
Pretty sure there are some other scenarios I forgot, but tried to cover the most important ones.

All in all, it's done, I had some fun. Thank you.

It's your turn. Enjoy!
SY, Andrei
