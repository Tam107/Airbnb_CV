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

@Service
public class Auth0Service {

    @Value("${okta.oauth2.client-id}")
    private String clientId;

    @Value("${okta.oauth2.client-secret}")
    private String clientSecret;

    @Value("${okta.oauth2.issuer}")
    private String domain;

    @Value("${auth0.application.role-landlord-id}")
    private String roleLandlordId;

    public void addLandLordRoleToUser(ReadUserDTO readUserDTO) {
        if (readUserDTO.authorities().stream().noneMatch(
                role -> role.equals(SecurityUtils.ROLE_LANDLORD))) {
            try {
                String accessToken = this.getAccessToken();
                assignRoleById(accessToken, readUserDTO.email(), readUserDTO.publicId(), roleLandlordId);

            } catch (Auth0Exception e) {
                throw new UserException(String.format("Not possible to be assign %s to %s", roleLandlordId, readUserDTO.publicId()));
            }
        }


    }

    private void assignRoleById(String accessToken, String email,
                                UUID publicId, String roleIdToAdd) throws Auth0Exception {
        ManagementAPI mgmt = ManagementAPI.newBuilder(domain, accessToken).build();
        Response<List<User>> auth0userByEmail = mgmt.users().listByEmail(email, new FieldsFilter()).execute();
        User user = auth0userByEmail.getBody()
                .stream().findFirst()
                .orElseThrow(() ->
                        new UserException(String.format("Cannot find user with publicId %s", publicId)));
        mgmt.roles().assignUsers(roleIdToAdd, List.of(user.getId())).execute();
    }

    private String getAccessToken() throws Auth0Exception {
        AuthAPI authAPI = AuthAPI.newBuilder(clientId, clientSecret, domain).build();
        TokenRequest tokenRequest = authAPI.requestToken(domain + "api/v2/");
        TokenHolder holder = tokenRequest.execute().getBody();
        return holder.getAccessToken();
    }

}
