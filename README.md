## UserService - REST API for managing users

Main technologies used are:

- Java 8
- MySQL 8
- Maven 3.6
- Spring Boot 2.4.1

Note: The application is build to run on application servers like Wildfly.

### Main features of the API are:

- Stores a user in DB
- Provides a paged list of users stored in DB
- Deletes a user

## Steps to Setup the Application

**1. Clone the application**

```shell
git clone https://github.com/VasAtanasov/user-service.git
```

Note: Next steps are executed in the root folder.

**2. Run the tests (Optional)**

```shell
mvn clean test
```

**3. Run the application with docker-compose**

```shell
docker-compose up --build
```

## Explore Rest APIs

The application defines following CRUD APIs.

| Method | Url | Description | Sample Valid Request Body | 
| ------ | --- | ---------- | --------------------------- |
| POST   | /app/v1/users | Stores new user in the database if not exists | [JSON](#create) |
| GET    | /app/v1/users?page={}&size={}&sort={} | Gets page of users | |
| DELETE | /app/v1/users/{uid} | Deletes user with provided uuid if exist | |

## Sample Valid JSON Request Bodies

##### <a id="create">Create user</a>

```http request
POST http://localhost:8080/app/v1/users HTTP/1.1
Accept: application/json
Content-Type: application/json

{
  "username": "fancyUsername",
  "firstName": "Peter",
  "lastName": "Peter"
}
```