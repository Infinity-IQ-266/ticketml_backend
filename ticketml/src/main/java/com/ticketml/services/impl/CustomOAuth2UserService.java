package com.ticketml.services.impl;

import com.ticketml.common.entity.User;
import com.ticketml.common.enums.Role;
import com.ticketml.repository.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");
        String googleId = oidcUser.getAttribute("sub");
        String picture = oidcUser.getAttribute("picture");
        LocalDateTime createdDate = LocalDateTime.now();

        User user = userRepository.findByGoogleId(googleId)
                .orElse(new User());

        user.setEmail(email);
        user.setGoogleId(googleId);
        user.setFirstName(name);
        user.setImageUrl(picture);
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(createdDate);
        }

        userRepository.save(user);

        return oidcUser;
    }
}
