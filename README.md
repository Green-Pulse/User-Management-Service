#  User Management Service

This microservice is responsible for **user operations** such as retrieving, updating, deleting user information, and interacting with **Keycloak Admin REST API**.

It works **together** with the `auth-service` and Keycloak, but focuses only on **user-level operations**, **roles**, and **account lifecycle management**.

---

##  Technologies

- Java 21  
- Spring Boot 3.4.4  
- Spring Security (JWT + Keycloak integration)  
- Kafka (for consuming registration events)  
- RestTemplate (to talk to Keycloak)  
- Docker (optional for infrastructure)


---

##  Security

- Uses JWT tokens issued by **Keycloak**
- Extracts roles from the token's `realm_access.roles`
- Authorization is done using `@PreAuthorize`

---

##  REST Endpoints

| Method | Endpoint                          | Description                    | Access              |
|--------|-----------------------------------|--------------------------------|---------------------|
| GET    | `/api/users`                      | Get all users                  | ADMIN, MANAGER      |
| GET    | `/api/users/me`                   | Get current user info          | AUTHENTICATED       |
| GET    | `/api/users/by-username/{u}`      | Get user by username           | ADMIN, MANAGER      |
| PUT    | `/api/users/by-username/{u}`      | Update user by username        | ADMIN, MANAGER      |
| DELETE | `/api/users/by-username/{u}`      | Delete user by username        | ADMIN               |
| POST   | `/api/users/logout`               | Revoke refresh token           | AUTHENTICATED       |

---

##  Keycloak Integration

This service uses **Keycloak Admin REST API** under the hood to:

- Fetch all users
- Fetch user by username
- Modify or delete users
- Revoke refresh tokens (`/logout`)

All requests to Keycloak are made using an `admin-cli` token which is retrieved dynamically.

---

##  Logout Endpoint

```http
POST /api/users/logout
Content-Type: application/x-www-form-urlencoded

refreshToken=refresh-token
```

Used to revoke refresh tokens when logging out a user.

---


##  Running Locally

```bash
# Make sure Kafka and Keycloak are running
./gradlew bootRun
```

Can test with Postman or via frontend once it passes the JWT token in the `Authorization` header.

---

##  Useful Resources

-  [Keycloak Admin API Docs](https://www.keycloak.org/docs-api/21.0.1/rest-api/index.html)
-  [JWT Debugger](https://jwt.io/)
-  [Prometheus](https://prometheus.io/)
-  [Grafana](https://grafana.com/)
-  [Spring Security JWT Docs](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)

---

##  TODOs

- [ ] Add unit tests for user role logic
- [ ] Add Swagger/OpenAPI documentation (optional)
- [ ] Monitoring
