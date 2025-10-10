package com.ticketml.services;

import com.ticketml.common.entity.User;

public interface JwtService {
    String generateToken(User user);

    String extractGoogleId(String token);

    boolean isTokenExpired(String token);

    boolean validateToken(String token);

}
