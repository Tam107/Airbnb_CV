package org.example.backend.infrastructure.config;

import org.example.backend.user.domain.Authority;
import org.example.backend.user.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SecurityUtils {

    public static final String ROLE_TENANT = "ROLE_TENANT";
    public static final String ROLE_LANDLORD = "ROLE_LANDLORD";

    public static final String CLAIMS_NAMESPACE = "https://cokecake.fr/roles";

    public static final String CLAIMS_ROLE = "role";

    public static User mapOauth2AttributesToUser(Map<String, Object> oauth2Attributes) {
        User user = new User();
        String sub = String.valueOf(oauth2Attributes.get("sub"));

        String username = null;
        if (oauth2Attributes.get("preferred_username") != null) {
            username = ((String) oauth2Attributes.get("preferred_username")).toLowerCase();
        }

        if (oauth2Attributes.get("given_name") != null) {
            user.setFirstName(((String) oauth2Attributes.get("given_name")).toLowerCase());
        } else if (oauth2Attributes.get("nickname") != null) {
            user.setFirstName(((String) oauth2Attributes.get("given_name")).toLowerCase());
        }

        if (oauth2Attributes.get("family_name") != null) {
            user.setLastName(((String) oauth2Attributes.get("family_name")).toLowerCase());
        }

        if (oauth2Attributes.get("email") != null) {
            user.setLastName(((String) oauth2Attributes.get("email")).toLowerCase());
        } else if (sub.contains("|") && (username != null && username.contains("@"))) {
            user.setEmail(username);
        } else {
            user.setEmail(sub);
        }
        if (oauth2Attributes.get("picture") != null) {
            user.setImageUrl(((String) oauth2Attributes.get("picture")));
        }

        if (oauth2Attributes.get(CLAIMS_NAMESPACE) != null) {
            Object authoritiesObj = oauth2Attributes.get(CLAIMS_NAMESPACE);
            List<String> authoritiesRaw;
            if (authoritiesObj instanceof List<?> authoritiesList) {
                authoritiesRaw = authoritiesList.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .collect(Collectors.toList());
            } else {
                authoritiesRaw = Collections.emptyList();
            }
            Set<Authority> authorities = authoritiesRaw.stream()
                    .map(authority -> {
                        Authority auth = new Authority();
                        auth.setName(authority);
                        return auth;
                    }).collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }

        return user;
    }

    public static List<SimpleGrantedAuthority> extractAuthorityFromClaims(Map<String, Object> claims) {
        return mapRolesToGrantedAuthorites(getRolesFromClaims(claims));
    }

    private static Collection<String> getRolesFromClaims(Map<String, Object> claims) {
        Object rolesObj = claims.get(CLAIMS_NAMESPACE);
        if (rolesObj instanceof List<?> rolesList) {
            return rolesList.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private static List<SimpleGrantedAuthority> mapRolesToGrantedAuthorites(Collection<String> roles) {
        return roles.stream()
                .filter(role -> role.startsWith("ROLE_"))
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public static boolean hasCurrentUserAnyOfAuthorities(String... authorities) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && getAuthorities(authentication)
                .anyMatch(authority -> Arrays.asList(authorities).contains(authority)));
    }

    private static Stream<String> getAuthorities(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication
                instanceof JwtAuthenticationToken jwtAuthenticationToken ?
                extractAuthorityFromClaims(jwtAuthenticationToken.getToken().getClaims()) :
                authentication.getAuthorities();
        return authorities.stream().map(GrantedAuthority::getAuthority);
    }
}