# TheBasic- A Spring boot user authentication boilerplate with S3 Image upload.

![alt text](https://github.com/313hemant313/TheBasic/blob/master/TheBasic.JPG?raw=true)

TheBasic uses a number of projects to work properly:

* [Spring Boot](https://spring.io/projects/spring-boot) - Open source Java-based framework
* [Maven](https://maven.apache.org/what-is-maven.html) Maven is a powerful project management tool that is based on POM (project object model)
* [MySQL](https://www.mysql.com/) - Open-source relational database management system.
* [AWS S3](https://aws.amazon.com/s3/) - Amazon Simple Storage Service (Amazon S3) is an object storage service [Used for storing images].
* [JJWT](https://github.com/jwtk/jjwt) - JSON Web Token Support For The JVM
* [Hibernate](https://hibernate.org/) - More than an ORM
* [HikariCP](https://github.com/brettwooldridge/HikariCP) - HikariCP is a "zero-overhead" production ready JDBC connection pool
* [Postman](https://www.postman.com/) Simplify each step of building an API and streamline collaboration so you can create better APIs

### Prerequisites
The following items should be installed in your system:
* Java 8 or newer.
* git command line tool (https://help.github.com/articles/set-up-git)
* Your preferred IDE 
  * [Eclipse](https://www.eclipse.org)
  * [Spring Tools Suite](https://spring.io/tools) (STS)
  * [VS Code](https://code.visualstudio.com)

### Exposed Rest Endpoints

| Feature | Route |
| ------ | ------ |
| User registration with ROLE_USER | [ /api/account/register ] |
| User login | [ /api/account/login ] |
| User token refresh | [ /api/account/token/refresh ] |
| User logout | [ /api/account/logout ] |
| User profile picture or some image upload | [ /api/images/upload ] |
| Get user details | [ /api/user/get ] |
| Update user password | [ /api/user/update/password ] |
| Update user details | [ /api/user/update/details ] |

## Database configuration
The application needs mysql database. Before running make sure you have added database config in src/main/resources/application.yaml
```sh
spring:             
  datasource:
    url: jdbc:mysql://sql12.freemysqlhosting.net:3306/sql12347916?useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false&useSSL=false&createDatabaseIfNotExist=true
    username: thebasicuser
    password: wrongpassword
```
For testing you can create some free online mysql db instance from websites like https://www.freemysqlhosting.net/

## AWS S3 configuration
In src/main/resources/application.yaml, Please add properties under 'amazonProperties'
```sh
amazonProperties:
  endpointUrl: https://s3.us-east-2.amazonaws.com
  accessKey: HEMANT-THEBASIC
  secretKey: YES-ITS-DUMMY
  bucketName: www.hemantdemo.com
```

## JWT configuration
Source src/main/resources/application.yaml
```sh
webapp:
  auth:
    tokenSecret: 926D96C90030DTHEBASICYA581BDBBC
    tokenExpirationMsec: 86400000
```

## Database connection pooling configuration [HikariCP]
Source src/main/resources/application.yaml
```sh
    hikari:
      minimumIdle: 5
      maximumPoolSize: 15
      connectionTimeout: 30000
      idleTimeout: 600000
      maxLifetime: 1800000
```

### To Run
```sh
./mvnw spring-boot:run
```
### To Build
```sh
./mvnw clean install
```

#### Also you can checkout examples in the postman-examples directory.

# License

The application is released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).
