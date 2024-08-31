package tdr.solutions.config;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service class responsible for handling API key-based authentication.
 *
 * <p>This service extracts the API key from the incoming {@link HttpServletRequest}, validates it,
 * and returns an {@link Authentication} object if the key is valid. If the key is missing or invalid,
 * it throws a {@link BadCredentialsException}.</p>
 *
 * <p>The service also includes special handling for requests to Swagger UI and API documentation endpoints,
 * allowing these requests to pass through with no authorities.</p>
 */
public class AuthenticationService {

    /** The name of the HTTP header that carries the API key. */
    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";

    /** The valid API key that is expected for authentication. */
    private static final String AUTH_TOKEN = "AMIR";

    /**
     * Retrieves and validates the API key from the incoming request.
     *
     * <p>If the request is for Swagger UI or API documentation, the method returns an unauthenticated
     * {@link ApiKeyAuthentication} token. Otherwise, it validates the API key and returns an
     * authenticated {@link ApiKeyAuthentication} token if the key is valid.</p>
     *
     * @param request the HTTP request containing the API key in its headers
     * @return an {@link Authentication} object representing the authenticated user
     * @throws BadCredentialsException if the API key is missing or invalid
     */
    public static Authentication getAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);

        // Allow requests to Swagger UI and API docs to pass through without authentication
        if (request.getRequestURL().toString().contains("swagger-ui") || request.getRequestURL().toString().contains("api-docs")) {
            return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
        }

        // Validate the API key
        if (apiKey == null || !apiKey.equals(AUTH_TOKEN)) {
            throw new BadCredentialsException("Invalid API Key");
        }

        // Return an authenticated token if the API key is valid
        return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}
