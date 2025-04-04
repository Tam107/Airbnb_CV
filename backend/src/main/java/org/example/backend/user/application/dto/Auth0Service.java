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
    private String roleLandLordId;

    /**
     * Adds the landlord role to a user if they don't already have it
     * @param readUserDTO the user information
     * */
    public void addLandLordRoleToUser(ReadUserDTO readUserDTO){
        // check if user already has the landlord role
        if (readUserDTO.authorities().stream().noneMatch(
                role -> role.equals(SecurityUtils.ROLE_LANDLORD)
        )){
            try{
                String accessToken = this.getAccessToken();

                // assign landlord role to the user
                assignRoleById(accessToken, readUserDTO.email(), readUserDTO.publicId(), roleLandLordId);

            }catch (Auth0Exception e){
                throw new UserException(String.format("Not possible to be assigned %s to %s", roleLandLordId, readUserDTO.publicId() ));
            }
        }
    }

    private void assignRoleById(String accessToken,
                                String email,
                                UUID publicId
                                ,String roleIdToAdd) throws Auth0Exception{
        // create Auth0 Management API client
        ManagementAPI mgmt = ManagementAPI.newBuilder(domain, accessToken).build();

        // find user by email
//      fieldsFilter filter the results received when calling an Auth0 endpoint
        Response<List<User>> auth0userByEmail = mgmt.users().listByEmail(email, new FieldsFilter()).execute();
        // get the first user matching the email
        User user = auth0userByEmail.getBody()
                .stream().findFirst()
                .orElseThrow(()->
                        new UserException(String.format("Cannot find user with publicId %s", publicId)));
        // Assign the role to the user
        mgmt.roles().assignUsers(roleIdToAdd, List.of(user.getId())).execute();
    }

    private String getAccessToken() throws Auth0Exception{
        // create Auth0 authentication API client
        AuthAPI authAPI = AuthAPI.newBuilder(clientId, clientSecret,domain).build();
        // request a token for management API
        TokenRequest tokenRequest = authAPI.requestToken(domain+"api/v2/");
        // Get token from the response
        TokenHolder holder = tokenRequest.execute().getBody();
        return holder.getAccessToken();
    }


}
