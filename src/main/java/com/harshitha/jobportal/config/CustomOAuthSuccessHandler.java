package com.harshitha.jobportal.config;

import com.harshitha.jobportal.entity.Users;
import com.harshitha.jobportal.services.UsersService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CustomOAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UsersService usersService;

    public CustomOAuthSuccessHandler(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = principal.getAttribute("email");
        System.out.println("The user " + email + " is logged in via OAuth2.");

        Optional<Users> user = usersService.getUserByEmail(email);
        if (user.isPresent()) {
            // Update authorities with database role
            Users u = user.get();
            List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
            authorities.add(new SimpleGrantedAuthority(u.getUserTypeId().getUserTypeName()));
            
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(
                    principal, 
                    authorities, 
                    oauthToken.getAuthorizedClientRegistrationId()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            response.sendRedirect("/dashboard/");
        } else {
            response.sendRedirect("/choose-role");
        }
    }
}
