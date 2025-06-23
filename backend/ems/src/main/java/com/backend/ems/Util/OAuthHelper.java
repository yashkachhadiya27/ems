package com.backend.ems.Util;

import com.backend.ems.DTO.GoogleOAuthUserDto;
import com.backend.ems.Entity.Register;
import com.backend.ems.Service.CustomOAuth2UserService;
import com.backend.ems.Service.Service_Interface.RegisterServiceInterface;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuthHelper {

        @Value("${spring.security.oauth2.client.registration.google.client-id}")
        private String clientId;

        @Value("${spring.security.oauth2.client.registration.google.client-secret}")
        private String clientSecret;

        @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
        private String userInfoUri;

        @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
        private String redirectUri;

        @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
        private String authorizationUri;

        private final CustomOAuth2UserService customOAuth2UserService;

        public final ClientRegistrationRepository clientRegistrationRepository;

        public final RegisterServiceInterface userService;

        public OidcUser processOidcUser(String authorizationCode, String clientRegistrationId) throws IOException {
                ClientRegistration clientRegistration = clientRegistrationRepository
                                .findByRegistrationId(clientRegistrationId);

                GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                                new NetHttpTransport(), new GsonFactory(),
                                clientId,
                                clientSecret,
                                authorizationCode,
                                redirectUri).execute();

                String idTokenValue = tokenResponse.getIdToken();
                String accessTokenValue = tokenResponse.getAccessToken();

                OAuth2AccessToken accessToken = new OAuth2AccessToken(
                                OAuth2AccessToken.TokenType.BEARER,
                                accessTokenValue,
                                null,
                                null);

                Map<String, Object> userAttributes = fetchUserAttributes(accessToken);

                Register user = userService.employeeByEmailOauth(userAttributes.get("email").toString());

                if (user == null) {
                        return null;
                        // GoogleOAuthUserDto googleOAuthUserDto = new GoogleOAuthUserDto(
                        // userAttributes.get("name").toString(),
                        // userAttributes.get("picture").toString(),
                        // userAttributes.get("email").toString());
                        // userService.saveFromGoogleDto(googleOAuthUserDto);

                }

                OidcIdToken oidcIdToken = new OidcIdToken(idTokenValue, new Date().toInstant(),
                                Instant.now().plusSeconds(tokenResponse.getExpiresInSeconds()), userAttributes);

                OidcUserRequest oidcUserRequest = new OidcUserRequest(clientRegistration, accessToken, oidcIdToken,
                                userAttributes);

                return customOAuth2UserService.loadUser(oidcUserRequest);
        }

        public Map<String, Object> fetchUserAttributes(OAuth2AccessToken accessToken) {
                RestTemplate restTemplate = new RestTemplate();

                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken.getTokenValue());

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                                userInfoUri,
                                HttpMethod.GET,
                                entity,
                                new ParameterizedTypeReference<Map<String, Object>>() {
                                });

                return response.getBody();
        }

        public String generateLoginLink() {
                return authorizationUri
                                + "?client_id=" + clientId
                                + "&redirect_uri=" + redirectUri
                                + "&response_type=code"
                                + "&scope=openid profile email";
        }
}
