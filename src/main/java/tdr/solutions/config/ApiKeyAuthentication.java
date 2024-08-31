package tdr.solutions.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

/**
 * Custom authentication token for API key-based authentication.
 *
 * <p>This class extends {@link AbstractAuthenticationToken} to provide a custom implementation
 * that handles authentication using an API key. It stores the API key as the principal
 * and manages the authentication state.</p>
 *
 * <p>This token is marked as authenticated immediately upon creation, assuming that the
 * presence of the API key and granted authorities is sufficient to authenticate the request.</p>
 */
public class ApiKeyAuthentication extends AbstractAuthenticationToken {

    /** The API key used for authentication. */
    private final String apiKey;

    /**
     * Constructs a new {@code ApiKeyAuthentication} token with the provided API key and authorities.
     *
     * @param apiKey the API key used for authentication
     * @param authorities the collection of granted authorities for this token
     */
    public ApiKeyAuthentication(String apiKey, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.apiKey = apiKey;
        setAuthenticated(true);  // Marks the token as authenticated
    }

    /**
     * Returns the credentials for this authentication, which in this case is always {@code null}.
     *
     * <p>This method is required by the {@link org.springframework.security.core.Authentication} interface,
     * but since API key authentication does not use a traditional credential (like a password),
     * it returns {@code null}.</p>
     *
     * @return {@code null} as there are no credentials associated with this token
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * Returns the principal for this authentication, which is the API key.
     *
     * @return the API key associated with this authentication token
     */
    @Override
    public Object getPrincipal() {
        return apiKey;
    }
}
