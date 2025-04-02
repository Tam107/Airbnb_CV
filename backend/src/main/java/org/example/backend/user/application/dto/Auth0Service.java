package org.example.backend.user.application.dto;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.FieldsFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Response;
import com.auth0.net.TokenRequest;
import org.example.backend.infrastructure.config.SecurityUtils;
import org.example.backend.user.application.UserException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * This service manages user roles in Auth0, specifically for adding the landlord role to users.
 */
@Service
public class Auth0Service {

    // Configuration values from application properties
    @Value("${okta.oauth2.client-id}")
    private String clientId;

    @Value("${okta.oauth2.client-secret}")
    private String clientSecret;

    @Value("${okta.oauth2.issuer}")
    private String domain;

    @Value("${auth0.application.role-landlord-id}")
    private String roleLandlordId;

    /**
     * Adds the landlord role to a user if they don't already have it.
     * @param readUserDTO The user information
     */
    public void addLandLordRoleToUser(ReadUserDTO readUserDTO) {
        // Check if user already has the landlord role
        if (readUserDTO.authorities().stream().noneMatch(
                role -> role.equals(SecurityUtils.ROLE_LANDLORD))) {
            try {
                // Get Auth0 access token for API access
                String accessToken = this.getAccessToken();
                // Assign the landlord role to the user
                assignRoleById(accessToken, readUserDTO.email(), readUserDTO.publicId(), roleLandlordId);

            } catch (Auth0Exception e) {
                // Handle error if role assignment fails
                throw new UserException(String.format("Not possible to be assign %s to %s", roleLandlordId, readUserDTO.publicId()));
            }
        }
    }

    /**
     * Finds a user by email and assigns a role to them in Auth0.
     * @param accessToken Auth0 API access token
     * @param email User's email address
     * @param publicId User's public ID (for error messaging)
     * @param roleIdToAdd ID of the role to assign
     * @throws Auth0Exception If API operations fail
     */
    private void assignRoleById(String accessToken, String email,
                                UUID publicId, String roleIdToAdd) throws Auth0Exception {
        // Create Auth0 Management API client
        ManagementAPI mgmt = ManagementAPI.newBuilder(domain, accessToken).build();
        // Find user by email
        Response<List<User>> auth0userByEmail = mgmt.users().listByEmail(email, new FieldsFilter()).execute();
        // Get the first user matching the email
        User user = auth0userByEmail.getBody()
                .stream().findFirst()
                .orElseThrow(() ->
                        new UserException(String.format("Cannot find user with publicId %s", publicId)));
        // Assign the role to the user
        mgmt.roles().assignUsers(roleIdToAdd, List.of(user.getId())).execute();
    }

    /**
     * Gets an access token for the Auth0 Management API.
     * @return The access token
     * @throws Auth0Exception If token retrieval fails
     */
    private String getAccessToken() throws Auth0Exception {
        // Create Auth0 authentication API client
        AuthAPI authAPI = AuthAPI.newBuilder(clientId, clientSecret, domain).build();
        // Request a token for the Management API
        TokenRequest tokenRequest = authAPI.requestToken(domain + "api/v2/");
        // Get the token from the response
        TokenHolder holder = tokenRequest.execute().getBody();
        return holder.getAccessToken();
    }
}