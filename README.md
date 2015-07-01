# ole-svc
Shaka's Ole web API for beacon subjects searching using spring boot framework and MongoDB.

## Prerequisits

Java 8.

MongoDB instance.

Beacon database has to be manually created.

## Build

``` mvn install ```

## Testing

```
mvn spring-boot:test
```

## Starting server

If Docker is not used:

Development: ``` mvn spring-boot:run ```

Production: ``` java -jar ?.jar ```

## License

ISC
