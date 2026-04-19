package com.harshitha.jobportal.services;

import com.harshitha.jobportal.entity.RecruiterProfile;
import com.harshitha.jobportal.entity.Users;
import com.harshitha.jobportal.repository.RecruiterProfileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecruiterProfileService {

    private final RecruiterProfileRepository recruiterRepository;
    private final UsersService usersService;

    public RecruiterProfileService(RecruiterProfileRepository recruiterRepository, UsersService usersService) {
        this.recruiterRepository = recruiterRepository;
        this.usersService = usersService;
    }

    public Optional<RecruiterProfile> getOne(Integer id) {
        return recruiterRepository.findById(id);
    }

    public RecruiterProfile addNew(RecruiterProfile recruiterProfile) {
        return recruiterRepository.save(recruiterProfile);
    }

    public RecruiterProfile getCurrentRecruiterProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Users users = usersService.getCurrentUser();
            Optional<RecruiterProfile> recruiterProfile = getOne(users.getUserId());
            return recruiterProfile.orElse(null);
        } else return null;
    }
}
