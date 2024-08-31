package tdr.solutions.config;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A custom filter that processes authentication for incoming HTTP requests.
 *
 * <p>This filter extends {@link GenericFilterBean} and is responsible for extracting authentication
 * information from each request, setting the authentication context, and handling any authentication
 * exceptions.</p>
 *
 * <p>If authentication fails, the filter responds with a 401 Unauthorized status and a JSON message
 * containing the exception's message.</p>
 */
public class AuthenticationFilter extends GenericFilterBean {

    /**
     * Filters incoming requests to authenticate them.
     *
     * <p>This method attempts to authenticate the request by invoking the {@link AuthenticationService#getAuthentication(HttpServletRequest)} method.
     * If authentication is successful, it sets the authentication in the {@link SecurityContextHolder}.
     * If authentication fails, it responds with a 401 Unauthorized status and an error message in JSON format.</p>
     *
     * @param request the {@link ServletRequest} object containing the client's request
     * @param response the {@link ServletResponse} object containing the filter's response
     * @param filterChain the {@link FilterChain} object that allows the filter to pass on the request and response to the next filter
     * @throws IOException if an I/O error occurs during the processing of the request
     * @throws ServletException if an error occurs during the filtering process
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            // Attempt to authenticate the request
            Authentication authentication = AuthenticationService.getAuthentication((HttpServletRequest) request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception exp) {
            // Handle authentication failure
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            try (PrintWriter writer = httpResponse.getWriter()) {
                writer.print(exp.getMessage());
                writer.flush();
            }
        }

        // Continue processing the filter chain
        filterChain.doFilter(request, response);
    }
}
