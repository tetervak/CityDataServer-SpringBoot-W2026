package ca.tetervak.citydata.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "oauth2_auth";

        // 1. Create the Scopes object and add your items
        Scopes oauthScopes = new Scopes()
                .addString("openid", "OpenID identity info")
                .addString("read", "Read access")
                .addString("write", "Write access");

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .description("OAuth2 flow for internal documentation access")
                                        .flows(new OAuthFlows()
                                                .authorizationCode(new OAuthFlow()
                                                        .authorizationUrl("http://localhost:9000/oauth2/authorize")
                                                        .tokenUrl("http://localhost:9000/oauth2/token")
                                                        .scopes(oauthScopes) // Pass the Scopes object here
                                                )
                                        )
                        )
                )
                .info(new Info()
                        .title("City Data API")
                        .version("1.0")
                        .description("API for accessing city data."));
    }

}
