package com.harshitha.jobportal.services;

import com.harshitha.jobportal.entity.JobSeekerProfile;
import com.harshitha.jobportal.entity.RecruiterProfile;
import com.harshitha.jobportal.entity.Users;
import com.harshitha.jobportal.repository.JobSeekerProfileRepository;
import com.harshitha.jobportal.repository.RecruiterProfileRepository;
import com.harshitha.jobportal.repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, JobSeekerProfileRepository jobSeekerProfileRepository, RecruiterProfileRepository recruiterProfileRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Users addNew(Users users) {
        users.setActive(true);
        users.setRegistrationDate(new Date(System.currentTimeMillis()));
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        Users savedUser = usersRepository.save(users);
        int userTypeId = users.getUserTypeId().getUserTypeId();

        if (userTypeId == 1) {
            recruiterProfileRepository.save(new RecruiterProfile(savedUser));
        }
        else {
            jobSeekerProfileRepository.save(new JobSeekerProfile(savedUser));
        }

        return savedUser;
    }

    public Users save(Users user) {
        return usersRepository.save(user);
    }

    public Object getCurrentUserProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            Object principal = authentication.getPrincipal();
            if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
                username = ((org.springframework.security.oauth2.core.user.OAuth2User) principal).getAttribute("email");
            }
            System.out.println("getCurrentUserProfile for: " + username);
            Users users = usersRepository.findByEmail(username).orElse(null);
            if (users == null) {
                System.out.println("User not found in DB: " + username);
                return null;
            }
            int userId = users.getUserId();
            if (Objects.equals(users.getUserTypeId().getUserTypeName(), "Recruiter")) {
                RecruiterProfile recruiterProfile = recruiterProfileRepository.findById(userId).orElse(new RecruiterProfile());
                return recruiterProfile;
            } else {
                JobSeekerProfile jobSeekerProfile = jobSeekerProfileRepository.findById(userId).orElse(new JobSeekerProfile());
                return jobSeekerProfile;
            }
        }

        return null;
    }

    public Users getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            Object principal = authentication.getPrincipal();
            if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User) {
                username = ((org.springframework.security.oauth2.core.user.OAuth2User) principal).getAttribute("email");
            }

            Users user = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Could not found " + "user"));
            return user;
        }

        return null;
    }

    public Users findByEmail(String currentUsername) {
        return usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("User not " +
                "found"));
    }

    public Optional<Users> getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }
    
}








