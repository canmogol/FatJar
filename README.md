# FatJar
FatJar simple API to quick prototyping and portable web services. See **Main::main** sample class for examples.

For scripting examples, see the main.js and main.py files under resources, building for scripting instructions can be found under "Building FatJar".


Build and coverage status:

[![Build Status](https://travis-ci.org/canmogol/FatJar.svg?branch=master)](https://travis-ci.org/canmogol/FatJar) [![codecov](https://codecov.io/gh/canmogol/FatJar/branch/master/graph/badge.svg)](https://codecov.io/gh/canmogol/FatJar) [![gitter](https://badges.gitter.im/canmogol/FatJar.svg)](https://gitter.im/FatJar/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![could not deploy][2]][1] [<img src="https://img.shields.io/maven-central/v/com.fererlab/FatJar*.svg?label=latest%20release"/>](http://search.maven.org/#search|ga|1|FatJar) 

  [1]: https://fatjar-travis.herokuapp.com/
  [2]: https://fatjar-travis.herokuapp.com/badgeFlat (FatJar Heroku Deployment Status)


Please check the interfaces like **HttpClient**, **JSON**, **Server** under **fatjar** package,
added sample **MyEntity** and **MyPOJO** classes under sample package for DB and JSON operations examples


Please find the swagger.yaml file as an example for service documentation,
you can edit yaml files with [Online Swagger Editor](http://editor.swagger.io/#/)
and you can convert yaml files to json to use with swagger-ui [Yaml to JSON Transformer](https://apimatic.io/transformer)



###Building FatJar

Building from source:

```sh
git clone https://github.com/canmogol/FatJar.git
cd FatJar
mvn clean install
```

if you want to use scripting engines, you should run the scripting profile, instead of "mvn clean install".

Please note that jython implementation adds around 40MB of code to code base.
```sh
mvn clean install -P scripting
```

You may run other language examples as;

##### Javascript
since javascript engine "nashorn" build in to jvm, javascript example should run without scripting profile enabled.
```sh
cd FatJar
java -jar target/FatJar-Example.jar src/main/resources/main.js
```

##### Python
python example needs the FarJar build with "scripting" profile enabled,
```sh
cd FatJar
java -jar target/FatJar-Example.jar src/main/resources/main.py
```

##### Scala
scala example needs sample-scala profile enabled, and you need to change the
```
<Main-Class>sample.Main</Main-Class>
```
value to
```
<Main-Class>sample.ScalaMain</Main-Class>
```
and after compilation you may run as
```sh
cd FatJar
java -jar target/FatJar-Example.jar
```


### Dependency

maven
```xml
<dependency>
    <groupId>com.fererlab</groupId>
    <artifactId>FatJar</artifactId>
    <version>1.3.0</version>
</dependency>
```

grails
```json
compile 'com.fererlab:FatJar:1.3.0'
```

sbt
```scala
libraryDependencies += "com.fererlab" % "FatJar" % "1.3.0"
```

### Usage Examples

to create and start the server
```javascript
Server.create().listen(80, "0.0.0.0").start();
```

Listen to an http method on a path;

java
```java
Server.create()
                .listen(80, "0.0.0.0")
                .get("/", (req, res) -> {
                    res.setContent("Welcome");
                    res.write();
                })
                .start();
```

javascript
```javascript
Server.create()
                .listen(8080, "0.0.0.0")
                .get("/", function (req, res) {
                    res.setContent("Welcome");
                    res.write();
                })
                .start();
```


python
```python
 Server.create()
              .listen(8080, "0.0.0.0") \
              .get("/", lambda req, res: (res.setContent("Welcome"), res.write())) \
              .start()
```


To JSON and from JSON example
```javascript
Server.create()
                .listen(80, "0.0.0.0")
                .get("/toJSON", (req, res) -> {
                    res.setContent(JSON.toJson(new MyPOJO("john", 101)));
                    res.write();
                })
                .post("/fromJSON", (req, res) -> {
                    MyPOJO myPOJO = JSON.fromJson(new String(req.getBody()), MyPOJO.class);
                    res.setContent(JSON.toJson(myPOJO));
                    res.write();
                })
                .start();
```

To make an Http request
```javascript
Server.create()
                .listen(80, "0.0.0.0")
                .get("/httpClient", (req, res) -> {
                    try {
                        String content = HttpClient.create()
                                .url("http://ip.jsontest.com/")
                                .method(HttpMethod.GET)
                                .send()
                                .getContentAsString();
                        res.setContent("got content: " + content);
                    } catch (HttpClient.HttpClientException e) {
                        res.setContent("error: " + e);
                    }
                    res.write();
                })
                .start();
```



### Sample Test Request

After Main::main start

```
request :   GET   http://localhost:80/
response:   Welcome
```

```
request :   GET   http://localhost:80/Hi
response:   type "http://localhost:80/Hi?name=john" in your browser
```

```
request :   GET   http://localhost:80/Hi?name=john
response:   Hello john
```

```
request :   GET   http://localhost:80/toJSON
response:   {"age":101,"name":"john"}
```

```
request :   GET   http://localhost:80/toXML?name=john
response:   <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <myPojo>
                <age>2147483647</age>
                <name>john</name>
            </myPojo>
```

Below example creates key value pairs in cache and increments 'number'
value by one on every request
```
request :   GET   http://localhost:80/cache
response:   {"number":1,"key":"value"}
```

Below example creates and writes to a file under tmp directory
```
request :   GET   http://localhost:80/file
response:   file content here!
```

```
request :   POST   http://localhost:80/fromJSON
            BODY   {"age":101,"name":"john"}
response:   {"age":101,"name":"john"}
```

```
request :   POST   http://localhost:80/fromXML
            BODY   <myPojo><name>john</name><age>27</age></myPojo>
response:   <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <myPojo>
                <age>27</age>
                <name>john</name>
            </myPojo>
```

A database method added, you may find the count, find, insert, update and delete methods under "/db" handler.
Please find the persistence.xml file under resources/META-INF for database connection parameters
```
request :   GET   http://localhost:80/db
response:   {"id":2,"name":"johnny"}
```

A mongodb method added, you may find the count, find, insert, update and delete methods under "/dbMongo" handler
Please find the mongodb.properties file under resources for database connection parameters
```
request :   GET   http://localhost:80/dbMongo
response:   {"address":"Elm Street","name":"john","objectId":{"counter":15787905,"date":1479883525000,"machineIdentifier":14269665,"processIdentifier":29177,"time":1479883525000,"timeSecond":1479883525,"timestamp":1479883525},"phone":"555-4343"}
```

Example exception throw method
```
request :   GET   http://localhost:80/throwException
response:   {"error": "fatjar.Server$ServerException: tojsonexception", "request": {...}, "status": "500"}
```

Below example will make a request to a uri that is not handled 
```
request :   GET   http://localhost:80/notfound
response:   {"error":"fatjar.Server$ServerException","status":"500"}
```

/badRequest will return html content, 
this content is created by the Status.STATUS_BAD_REQUEST handler,
registered with the .register(Status.STATUS_BAD_REQUEST, ...) method
```
request :   GET   http://localhost:80/badRequest
response:   <h1>BAD REQUEST!</h1> 
```

```
request :   GET   http://localhost:80/httpClient
response:   got content: {"ip": "123.123.123.123"}
```

TEA and AES encryption example
```
request :   GET   http://localhost:80/encrypt
response:   got content: TEA( clear: 123456 encrypted/decrypted: 123456)
                         AES (clear: 123456 encrypted/decrypted: 123456)
```

Encrypted and encoded set cookie example
```
request :   GET   http://localhost:80/setCookie
response:   got content: cookie set
            check cookie: FAT_JAR_EXAMPLE_APP, it is encoded, signed
            you may find COOKIE_CONTENT as the content of the cookie
            and COOKIE_SIGN_KEY as the signed part of the cookie.
            Decoded COOKIE_CONTENT contains EncodedKey which is clear
            EncryptedKey_COOKIE_ENCRYPTED is encrypted.
```

Encrypted and encoded get cookie example
```
request :   GET   http://localhost:80/getCookie
response:   got content: EncodedKey:EncodedValue1111 - EncryptedKey:EncryptedValue2222
            these two are the decoded and decrypted values
            which set at the /setCookie call
```
