# dropwizard-auth-jwt

## Status

[![Build Status](https://travis-ci.com/CodeByAB/dropwizard-auth-jwt.svg?branch=master)](https://travis-ci.com/CodeByAB/dropwizard-auth-jwt)

## How to configure the JwtBundle

In the application file, add the `JwtBundle` and `JwtTokenGenerator`:

```java
@Getter
public class MyApplication extends Application<MyConfiguration> {
    private MyConfiguration configuration;
    private JwtTokenGenerator jwtTokenGenerator;

    public static void main(final String[] args) {
        new MyApplication().run(args);
    }

    @Override
    public String getName() {
        return "myApplication";
    }

    @Override
    public void initialize(final Bootstrap<MyConfiguration> bootstrap) {

        bootstrap.addBundle(new JwtBundle());
    }
    
    @Override
    public void run(final MyConfiguration configuration, final Environment environment) {
        this.configuration = configuration;
        this.jwtTokenGenerator = new JwtTokenGenerator(configuration.jwt);
    }    
}
```

`MyConfiguration` needs to implement `JwtConfig`:

```java
@Log4j
public class MyConfiguration extends Configuration implements JwtConfig {

    @Valid
    @NotNull
    @JsonProperty("jwt")
    public JwtConfiguration jwt = new JwtConfiguration();

    @Override
    public JwtConfiguration getJwtConfiguration() {
        return jwt;
    }
}

``` 

and the `my.yml` file looks like this:

```yaml
server:
  type: simple
  applicationContextPath: /api
  adminContextPath: /admin
  connector:
    type: http
    port: 3000

logging:
  level: INFO
  loggers:
    se.codeby: DEBUG
  appenders:
    - type: console

jwt:
  key: thisIstheKey12sdfasdfsadfsjlkasdfhoweinadsfnlköadfksad34asdfasdf5

```

## JwtConfiguration

Following attributes can be changed

```yaml
jwt:
  key: thisIstheKey12sdfasdfsadfsjlkasdfhoweinadsfnlköadfksad34asdfasdf5
  allowedClockSkewInSeconds: 300
```

`allowedClockSkewInSeconds` is in seconds (default value 300)

## Annotate the protected resources

On Class level or method level you can use following annotations,  methods has priority over class annotations.

- `@PermitAll`
- `@DenyAll`
- `@RolesAllowed("ALLOW_ME")`

Your method should have a parameter annotated with

- `@Auth` JwtUser user

See example below:

```java
@DenyAll
@Path("/secure")
@Produces(MediaType.APPLICATION_JSON)
public class SecureResource {

    private MyApplication application;

    @Context
    private UriInfo uriInfo;

    public SecureResource(MyApplication application) {
        this.application = application;
    }

    @GET
    @RolesAllowed("SECURE_ROLE")
    public Response fetchAll(@Auth JwtUser user) {
        return Response.ok().build();
    }
}
```

## Generate the `JWT token`

Can be done as simple as:

```java
@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Log4j
public class LoginResource {

    private final MyApplication application;

    public LoginResource(LoginApplication application) {
        this.application = application;
    }

   

    @POST
    public Response login(@Valid @NotNull Login login) {
        return Response.ok(
            // Do your authentication here.. and return the users roles
            ImmutableMap.of(
                "token", application.getJwtTokenGenerator().generate("1", "Daniel", ImmutableList.of("SECURE_ROLE"))
            )
        ).build();
    }
}

```

## Example code on client side

To fetch out the JWT token:

```bash
curl -X "POST" "http://localhost:3000/api/login" \
     -H 'Content-Type: application/json' \
     -d $'{
  "email": "daniel@test.com",
  "password": "1234qwert"
}'

```

To call the secure resource:

```bash
curl "http://localhost:3000/api/secure" \
     -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZXMiOiJVU0VSIiwidXNlciI6IkRhbmllbCIsImlhdCI6MTU1MjU0OTk2NiwianRpIjoiZ1RmT1ljVDZQQ0tSYjJxWFhYcDRFZyJ9.F4r0b8_Aj75LqPnsY_-Cc8GdT2k93B-Iwq4DovxLUxU'

```
