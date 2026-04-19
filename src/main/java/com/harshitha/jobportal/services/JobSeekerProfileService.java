package com.harshitha.jobportal.services;

import com.harshitha.jobportal.entity.JobSeekerProfile;
import com.harshitha.jobportal.entity.Users;
import com.harshitha.jobportal.repository.JobSeekerProfileRepository;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobSeekerProfileService {

    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final UsersService usersService;

    public JobSeekerProfileService(JobSeekerProfileRepository jobSeekerProfileRepository, UsersService usersService) {
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.usersService = usersService;
    }

    public Optional<JobSeekerProfile> getOne(Integer id) {
        return jobSeekerProfileRepository.findById(id);
    }

    public JobSeekerProfile addNew(JobSeekerProfile jobSeekerProfile) {
        return jobSeekerProfileRepository.save(jobSeekerProfile);
    }

    public JobSeekerProfile getCurrentSeekerProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            Users users = usersService.getCurrentUser();
            Optional<JobSeekerProfile> seekerProfile = getOne(users.getUserId());
            return seekerProfile.orElse(null);
        } else return null;

    }
}
