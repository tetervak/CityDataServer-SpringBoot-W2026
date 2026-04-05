package ca.tetervak.citydata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * CHAIN 1: Swagger UI protected by OAuth2 Login (SSO)
     * Redirects to the Authorization Server (Port 9000) for authentication.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain uiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html",
                        "/secret-page", "/oauth2/**", "/login/**"
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. Let the documentation be public
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // 2. CRITICAL: The OAuth2 endpoints MUST be public
                        // so the handshake can start and finish!
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()

                        // 3. Only the actual content requires a session
                        .requestMatchers("/secret-page").authenticated()

                        // 4. Catch-all for this matcher
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/resource-server-reg")
                );

        return http.build();
    }
    /**
     * CHAIN 2: REST API protected by Bearer Tokens
     */
    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        //.requestMatchers(HttpMethod.POST, "/api/cities").hasAuthority("SCOPE_write")
                        // or .hasRole("USER")

                        //.requestMatchers(HttpMethod.POST, "/api/cities/**").hasAuthority("SCOPE_write")
                        //.requestMatchers(HttpMethod.GET, "/api/cities/**").hasAuthority("SCOPE_read")

                        //.anyRequest().authenticated()

                        // Use the filter for general path protection
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:9000", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        // Default: looks at "scope" or "scp" and adds "SCOPE_" prefix

        JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthoritiesClaimName("roles"); // Look at our custom "roles" claim
        rolesConverter.setAuthorityPrefix(""); // Roles already have "ROLE_" from our Customizer

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // MERGE BOTH: Get the scopes AND the roles
            Collection<GrantedAuthority> authorities = scopesConverter.convert(jwt);
            authorities.addAll(rolesConverter.convert(jwt));
            return authorities;
        });
        return converter;
    }
}