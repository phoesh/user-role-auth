# User-Role-Auth

- Run the main function in Application.Class to start the HTTP server.
## Requirement
- Do not use any web frameworks like Spring, Spring-boot, Jersey or else.
## API

* Create user
    - /user/create
    - Params [name, password]
* Delete user
    - /user/delete
    - Params [name]
* Create role
    - /user/create_role
    - Params [role]
* Delete role
    - /user/delete_role
    - Params [role]
* Add role to user
    - /user/add_user_role
    - Params [name, role]
* Authenticate
    - /user/auth
    - Params [name, password]
    - The token will be expired after 2 hours.
* Invalidate
    - /user/invalidate_token
    - Params [authToken]
* Check role
    - /user/check_role
    - Params [authToken, role]
* All roles
    - /user/all_user_roles
    - Params [authToken]

## Example

#### Create user

- **curl -d '{"name":"user1", "password":"12345678"}' -H "Content-Type: application/json" -X
  POST http://localhost:8080/user/create**

# Dependencies and Purposes

````
<dependencies>
    <!-- Use this dependency to scan annotations -->
    <dependency>
        <groupId>org.reflections</groupId>
        <artifactId>reflections</artifactId>
        <version>${reflections.version}</version>
    </dependency>

    <!-- Auth token -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>${io.jsonwebtoken.version}</version>
    </dependency>

    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>${io.jsonwebtoken.version}</version>
        <scope>runtime</scope>
    </dependency>

    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId> <!-- or jjwt-gson if Gson is preferred -->
        <version>${io.jsonwebtoken.version}</version>
        <scope>runtime</scope>
    </dependency>

    <!-- Check String whether is empty -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>${commons-lang3.version}</version>
    </dependency>

    <!-- JSON -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-core</artifactId>
        <version>${jackson.version}</version>
    </dependency>

    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>${jackson.version}</version>
    </dependency>

    <!-- log4j2 -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>${slf4j.version}</version>
    </dependency>

    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>jul-to-slf4j</artifactId>
        <version>${slf4j.version}</version>
    </dependency>

    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>${log4j.version}</version>
    </dependency>

    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
        <version>${log4j.version}</version>
    </dependency>

    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-slf4j-impl</artifactId>
        <version>${log4j.version}</version>
    </dependency>
    
    <!-- Unit testing -->
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>${junit.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>
````