package org.example.backend.user.presentation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.backend.user.application.UserService;
import org.example.backend.user.application.dto.ReadUserDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthResource {

    private final UserService userService;

    private final ClientRegistration registration;

    public AuthResource(UserService userService, ClientRegistrationRepository registration){
        this.userService = userService;
        this.registration = registration.findByRegistrationId("okta");
    }

    public ResponseEntity<ReadUserDTO> getAuthenticatedUser(@AuthenticationPrincipal OAuth2User user, @RequestParam boolean forceResync){
        if (user == null){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }else {
            userService.syncWithIdp(user, forceResync);
            ReadUserDTO connectedUser = userService.getAuthenticatedUserFromSecurityContext();
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request){
        String issuerUri = registration.getProviderDetails().getIssuerUri();
        String originUrl = request.getHeader(HttpHeaders.ORIGIN);
        Object[] params = {issuerUri, registration.getClientId(), originUrl};
        String logoutUrl = MessageFormat.format("{0}v2/logout?clinet_id={1}&returnTo={2}",params);
        request.getSession().invalidate();
        return ResponseEntity.ok().body(Map.of("logoutUrl", logoutUrl));
    }


}
