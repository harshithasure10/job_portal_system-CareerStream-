package com.harshitha.jobportal.controller;

import com.harshitha.jobportal.entity.*;
import com.harshitha.jobportal.services.JobPostActivityService;
import com.harshitha.jobportal.services.JobSeekerApplyService;
import com.harshitha.jobportal.services.JobSeekerSaveService;
import com.harshitha.jobportal.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class JobPostActivityController {

    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;

    public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService, JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @GetMapping("/dashboard/")
    public String searchJobs(Model model,
                             @RequestParam(value = "job", required = false) String job,
                             @RequestParam(value = "location", required = false) String location,
                             @RequestParam(value = "type", required = false) List<String> type,
                             @RequestParam(value = "remote", required = false) List<String> remote,
                             @RequestParam(value = "today", required = false) boolean today,
                             @RequestParam(value = "days7", required = false) boolean days7,
                             @RequestParam(value = "days30", required = false) boolean days30,
                             @RequestParam(value = "page", defaultValue = "1") int page

    ) {

        model.addAttribute("type", type);
        model.addAttribute("remote", remote);
        model.addAttribute("today", today);
        model.addAttribute("days7", days7);
        model.addAttribute("days30", days30);

        model.addAttribute("job", job);
        model.addAttribute("location", location);

        LocalDate searchDate = null;
        Page<JobPostActivity> jobPage = null;
        boolean dateSearchFlag = true;
        boolean remoteSearchFlag = true;
        boolean typeSearchFlag = true;

        if (days30) {
            searchDate = LocalDate.now().minusDays(30);
        } else if (days7) {
            searchDate = LocalDate.now().minusDays(7);
        } else if (today) {
            searchDate = LocalDate.now();
        } else {
            dateSearchFlag = false;
        }

        if (type == null || type.isEmpty()) {
            type = Arrays.asList("Part-Time", "Full-Time", "Freelance");
            typeSearchFlag = false;
        }

        if (remote == null || remote.isEmpty()) {
            remote = Arrays.asList("Office-Only", "Remote-Only", "Partial-Remote");
            remoteSearchFlag = false;
        }

        Pageable pageable = PageRequest.of(page - 1, 5);

        if (!dateSearchFlag && !remoteSearchFlag && !typeSearchFlag && !StringUtils.hasText(job) && !StringUtils.hasText(location)) {
            jobPage = jobPostActivityService.getAll(pageable);
        } else {
            jobPage = jobPostActivityService.search(job, location, type, remote, searchDate, pageable);
        }

        Object currentUserProfile = usersService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            if (currentUserProfile == null) {
                return "redirect:/logout";
            }
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);
            if (currentUserProfile instanceof RecruiterProfile) {
                Page<RecruiterJobsDto> recruiterJobs = jobPostActivityService.getRecruiterJobs(((RecruiterProfile) currentUserProfile).getUserAccountId(), pageable);
                model.addAttribute("jobPost", recruiterJobs.getContent());
                model.addAttribute("totalItems", recruiterJobs.getTotalElements());
                model.addAttribute("totalPages", recruiterJobs.getTotalPages());
                model.addAttribute("currentPage", page);
            } else {
                List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getCandidatesJobs((JobSeekerProfile) currentUserProfile);
                List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);

                List<JobPostActivity> jobPost = jobPage.getContent();
                for (JobPostActivity jobActivity : jobPost) {
                    boolean exist = jobSeekerApplyList.stream()
                            .anyMatch(a -> Objects.equals(jobActivity.getJobPostId(), a.getJob().getJobPostId()));
                    boolean saved = jobSeekerSaveList.stream()
                            .anyMatch(s -> Objects.equals(jobActivity.getJobPostId(), s.getJob().getJobPostId()));
                    
                    jobActivity.setIsActive(exist);
                    jobActivity.setIsSaved(saved);
                }
                model.addAttribute("jobPost", jobPost);
                model.addAttribute("totalItems", jobPage.getTotalElements());
                model.addAttribute("totalPages", jobPage.getTotalPages());
                model.addAttribute("currentPage", page);
            }
        }

        model.addAttribute("user", currentUserProfile);

        return "dashboard";
    }

    @GetMapping("global-search/")
    public String globalSearch(Model model,
                               @RequestParam(value = "job", required = false) String job,
                               @RequestParam(value = "location", required = false) String location,
                               @RequestParam(value = "type", required = false) List<String> type,
                               @RequestParam(value = "remote", required = false) List<String> remote,
                               @RequestParam(value = "today", required = false) boolean today,
                               @RequestParam(value = "days7", required = false) boolean days7,
                               @RequestParam(value = "days30", required = false) boolean days30,
                               @RequestParam(value = "page", defaultValue = "1") int page) {

        model.addAttribute("type", type);
        model.addAttribute("remote", remote);
        model.addAttribute("today", today);
        model.addAttribute("days7", days7);
        model.addAttribute("days30", days30);

        model.addAttribute("job", job);
        model.addAttribute("location", location);

        LocalDate searchDate = null;
        Page<JobPostActivity> jobPage = null;
        boolean dateSearchFlag = true;
        boolean remoteSearchFlag = true;
        boolean typeSearchFlag = true;

        if (days30) {
            searchDate = LocalDate.now().minusDays(30);
        } else if (days7) {
            searchDate = LocalDate.now().minusDays(7);
        } else if (today) {
            searchDate = LocalDate.now();
        } else {
            dateSearchFlag = false;
        }

        if (type == null || type.isEmpty()) {
            type = Arrays.asList("Part-Time", "Full-Time", "Freelance");
            typeSearchFlag = false;
        }

        if (remote == null || remote.isEmpty()) {
            remote = Arrays.asList("Office-Only", "Remote-Only", "Partial-Remote");
            remoteSearchFlag = false;
        }

        Pageable pageable = PageRequest.of(page - 1, 5);

        if (!dateSearchFlag && !remoteSearchFlag && !typeSearchFlag && !StringUtils.hasText(job) && !StringUtils.hasText(location)) {
            jobPage = jobPostActivityService.getAll(pageable);
        } else {
            jobPage = jobPostActivityService.search(job, location, type, remote, searchDate, pageable);
        }

        model.addAttribute("jobPost", jobPage.getContent());
        model.addAttribute("totalItems", jobPage.getTotalElements());
        model.addAttribute("totalPages", jobPage.getTotalPages());
        model.addAttribute("currentPage", page);

        model.addAttribute("user", usersService.getCurrentUserProfile());

        return "global-search";
    }

    @GetMapping("/dashboard/add")
    public String addJobs(Model model) {
        model.addAttribute("jobPostActivity", new JobPostActivity());
        model.addAttribute("user", usersService.getCurrentUserProfile());
        return "add-jobs";
    }

    @PostMapping("/dashboard/addNew")
    public String addNew(JobPostActivity jobPostActivity, Model model) {

        Users user = usersService.getCurrentUser();
        if (user != null) {
            jobPostActivity.setPostedById(user);
        }
        jobPostActivity.setPostedDate(new Date());
        model.addAttribute("jobPostActivity", jobPostActivity);
        jobPostActivityService.addNew(jobPostActivity);
        return "redirect:/dashboard/";
    }

    @PostMapping("dashboard/edit/{id}")
    public String editJob(@PathVariable("id") int id, Model model) {

        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
        model.addAttribute("jobPostActivity", jobPostActivity);
        model.addAttribute("user", usersService.getCurrentUserProfile());
        return "add-jobs";
    }

    @PostMapping("/dashboard/deleteJob/{id}")
    public String deleteJob(@PathVariable("id") int id) {

        jobPostActivityService.deleteById(id);
        return "redirect:/dashboard/";
    }

    @GetMapping("/dashboard/for-you")
    public String recommendedJobs(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {
        Object currentUserProfile = usersService.getCurrentUserProfile();
        if (currentUserProfile instanceof JobSeekerProfile) {
            List<Skills> skills = ((JobSeekerProfile) currentUserProfile).getSkills();
            List<String> skillNames = skills.stream().map(Skills::getName).collect(java.util.stream.Collectors.toList());
            
            List<JobPostActivity> recommendedJobs = jobPostActivityService.getRecommendedJobs(skillNames);
            
            // Handle pagination manually for now since service returns a list
            int pageSize = 5;
            int totalItems = recommendedJobs.size();
            int totalPages = (int) Math.ceil((double) totalItems / pageSize);
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, totalItems);
            
            List<JobPostActivity> pageContent = (start < totalItems) ? recommendedJobs.subList(start, end) : new java.util.ArrayList<>();
            
            // Mark applied/saved jobs
            List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getCandidatesJobs((JobSeekerProfile) currentUserProfile);
            List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);

            for (JobPostActivity jobActivity : pageContent) {
                boolean exist = jobSeekerApplyList.stream()
                        .anyMatch(a -> Objects.equals(jobActivity.getJobPostId(), a.getJob().getJobPostId()));
                boolean saved = jobSeekerSaveList.stream()
                        .anyMatch(s -> Objects.equals(jobActivity.getJobPostId(), s.getJob().getJobPostId()));
                
                jobActivity.setIsActive(exist);
                jobActivity.setIsSaved(saved);
            }

            model.addAttribute("jobPost", pageContent);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("currentPage", page);
            model.addAttribute("forYouActive", true);
            model.addAttribute("user", currentUserProfile);
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            model.addAttribute("username", authentication.getName());
            
            return "dashboard";
        }
        return "redirect:/dashboard/";
    }

}








