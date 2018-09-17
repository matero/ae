package ae;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.testng.Reporter;
import org.testng.annotations.BeforeSuite;

public abstract class Specification {

    private static final AtomicBoolean haveInitiallizedRestAssured = new AtomicBoolean(false);

    private final String defaultUserEmail;
    private Map<String, String> loginCookies;

    protected Specification()
    {
        this("test@example.com");
    }

    protected Specification(final String defaultUserEmail)
    {
        this.defaultUserEmail = defaultUserEmail;
    }

    @BeforeSuite
    public void setUpSuite()
    {
        if (!haveInitiallizedRestAssured.get()) {
            haveInitiallizedRestAssured.set(true);
            initRestAssured();
        }
    }

    protected void initRestAssured()
    {
        final String port = System.getProperty("server.port");
        if (port != null) {
            try {
                RestAssured.port = Integer.valueOf(port);
            } catch (final NumberFormatException e) {
                throw new SpecificationException("'server.port' if be defined, must be an Integer", e);
            }
        }
        Reporter.log("using RestAssured.port = " + RestAssured.port, true);
        final String basePath = System.getProperty("server.base");
        if (basePath != null) {
            RestAssured.basePath = basePath;
        }
        Reporter.log("using RestAssured.basePath = " + RestAssured.basePath, true);
        final String baseHost = System.getProperty("server.host");
        if (baseHost != null) {
            RestAssured.baseURI = baseHost;
        }
        Reporter.log("using RestAssured.baseURI = " + RestAssured.baseURI, true);
    }

    protected RequestSpecification givenUserIsLoggedIn()
    {
        return givenUserIsLoggedIn(defaultUserEmail, asUser, "/");
    }

    protected RequestSpecification givenUserIsLoggedIn(final boolean isAdmin)
    {
        return givenUserIsLoggedIn(defaultUserEmail, isAdmin, "/");
    }

    protected RequestSpecification givenUserIsLoggedIn(final String continueUrl)
    {
        return givenUserIsLoggedIn(defaultUserEmail, asUser, continueUrl);
    }

    protected RequestSpecification givenUserIsLoggedIn(final String continueUrl, final boolean isAdmin)
    {
        return givenUserIsLoggedIn(defaultUserEmail, isAdmin, continueUrl);
    }

    protected RequestSpecification givenUserIsLoggedIn(final String email, final boolean isAdmin,
                                                       final String continueUrl)
    {
        if (loginCookies == null) {
            final String encodedEmail;
            try {
                encodedEmail = URLEncoder.encode(email, "UTF-8");
            } catch (final UnsupportedEncodingException e) {
                throw new SpecificationException("login email '" + email + "' could not be encoded", e);
            }
            Reporter.log("using encodedEmail = " + encodedEmail, true);

            final String encodedContinueUrl;
            try {
                encodedContinueUrl = URLEncoder.encode(continueUrl, "UTF-8");
            } catch (final UnsupportedEncodingException e) {
                throw new SpecificationException("login continueUrl '" + continueUrl + "' could not be encoded", e);
            }
            Reporter.log("using encodedContinueUrl = " + encodedContinueUrl, true);
            final RequestSpecification r = given().param("email", encodedEmail).
                    param("continue", encodedContinueUrl).
                    param("action", "Log+In");
            if (isAdmin) {
                r.param("admin", "on");
            }
            final Response response = r.log().all().when().post("/_ah/login");
            response.then().statusCode(302).log().all();
            loginCookies = response.cookies();
        }
        return given().cookies(loginCookies);
    }

    protected static final String withEmail(final String value)
    {
        return value;
    }

    protected static final boolean asAdmin = true;

    protected static final boolean asUser = false;

    protected static final String andContinuesToUrl(final String value)
    {
        return value;
    }

    protected static final LoggersFactory makeLogger = LoggersFactory.INSTANCE;

    protected static final RequestSpecification given()
    {
        return RestAssured.given();
    }
}
