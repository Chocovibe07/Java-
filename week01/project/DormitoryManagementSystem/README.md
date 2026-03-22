# dorm-repair-system

A minimal layered Java project for dormitory repair management with a MyBatis-ready structure.

## Structure

- `com.gdut.repair.Main`
- `com.gdut.repair.controller`
- `com.gdut.repair.service`
- `com.gdut.repair.service.impl`
- `com.gdut.repair.mapper`
- `com.gdut.repair.entity`
- `com.gdut.repair.util`
- `com.gdut.repair.constant`
- `src/main/resources/mybatis-config.xml`

## Notes

- User registration in `Main` is connected to MySQL through MyBatis.
- SQL mappers use the tables `user` and `repair`.

## Run

```bash
mvn test
mvn -q exec:java
```

