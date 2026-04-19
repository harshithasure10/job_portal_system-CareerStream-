package com.harshitha.jobportal.controller;

import com.harshitha.jobportal.entity.Users;
import com.harshitha.jobportal.entity.UsersType;
import com.harshitha.jobportal.services.UsersService;
import com.harshitha.jobportal.services.UsersTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
public class OAuth2Controller {

    private final UsersService usersService;
    private final UsersTypeService usersTypeService;

    public OAuth2Controller(UsersService usersService, UsersTypeService usersTypeService) {
        this.usersService = usersService;
        this.usersTypeService = usersTypeService;
    }

    @GetMapping("/choose-role")
    public String chooseRole(Model model) {
        return "choose-role";
    }

    @GetMapping("/register/social/save")
    public String saveSocialUser(@RequestParam("role") String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = principal.getAttribute("email");

        Users users = new Users();
        users.setEmail(email);
        users.setPassword(UUID.randomUUID().toString()); // Random password for OAuth users

        List<UsersType> types = usersTypeService.getAll();
        for (UsersType type : types) {
            if (Objects.equals(type.getUserTypeName(), role)) {
                users.setUserTypeId(type);
                break;
            }
        }

        Users savedUser = usersService.addNew(users);
        
        // Update SecurityContext with the new role
        List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
        authorities.add(new SimpleGrantedAuthority(savedUser.getUserTypeId().getUserTypeName()));
        
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(
                principal, 
                authorities, 
                oauthToken.getAuthorizedClientRegistrationId()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        return "redirect:/dashboard/";
    }
}
