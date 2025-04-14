package org.example.backend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    // initialize configuration
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception{
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName(null);
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.GET, "/api/tenant-listing/get-all-by-category").permitAll()
                                .requestMatchers(HttpMethod.GET,"/api/tenant-listing/get-one").permitAll()
                                .requestMatchers(HttpMethod.GET, "assests/*").permitAll()
                                .anyRequest()
                .authenticated()
                // configure Cross-Site Request Forgery (CSRF) protection
        ).csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(requestHandler))
                .oauth2Login(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .oauth2Client(Customizer.withDefaults());

        return http.build();

    }

        // when the user login, pass spring security role to user -> extract from JWT to Spring security
        //    GrantedAuthoritiesMapper is a Spring Security interface
        //    that allows you to transform the authorities (roles and permissions)
    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper(){
        return authorities -> {
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        // OIDC is an identity layer on top of OAuth 2.0, commonly used for SSO (Single Sign-On)
            authorities.forEach(grantedAuthority -> {
                if (grantedAuthority instanceof OidcUserAuthority oidcUserAuthority){
                    grantedAuthorities.addAll(SecurityUtils.extractAuthorityFromClaims(oidcUserAuthority.getUserInfo().getClaims()));
                }
            });
            return grantedAuthorities;
        };
    }

}
