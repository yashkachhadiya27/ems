package com.backend.ems.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import com.backend.ems.Entity.Register;
import com.backend.ems.Exception.EmployeeNotFoundException;
import com.backend.ems.Repository.RegisterRepository;
import com.backend.ems.Service.Service_implementation.RegisterServiceImpl;

@Service
public class CustomOAuth2UserService extends OidcUserService {
    @Autowired
    private RegisterRepository userRepo;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = (String) oidcUser.getAttributes().get("email");

        Register user = userRepo.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found."));

        Set<GrantedAuthority> authorities = new HashSet<>(user.getAuthorities());

        return new DefaultOidcUser(
                authorities,
                oidcUser.getIdToken(),
                "email");
    }

}
