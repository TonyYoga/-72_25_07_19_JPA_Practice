# Lesson #72 25/07/2019 JPA Practice

### DataBase connection
Change [application.properties](https://github.com/java-3-haifa/-72_25_07_19_JPA_Practice/blob/master/src/main/resources/application.properties) for connect your DataBase

```properties
>>>>> spring.datasource.url=jdbc:mysql://[database_host]:[database_port]/[database_name]?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
>>>>> spring.datasource.username=[Your database user name]
>>>>> spring.datasource.password=[Your database password]
```
### HTTP Requests Examples
[Requests for admin](https://github.com/java-3-haifa/-72_25_07_19_JPA_Practice/blob/master/RequestsAdmin.http)
[Requests for users](https://github.com/java-3-haifa/-72_25_07_19_JPA_Practice/blob/master/RequestsUser.http)

### DataBase dump

[Database dump file](https://github.com/java-3-haifa/-72_25_07_19_JPA_Practice/blob/master/database_dump.sql)

### DataBase Schema
![database schema](https://github.com/java-3-haifa/-72_25_07_19_JPA_Practice/blob/master/database_schema.png)
