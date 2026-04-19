package com.harshitha.jobportal.services;

import com.harshitha.jobportal.entity.*;
import com.harshitha.jobportal.repository.JobPostActivityRepository;
import com.harshitha.jobportal.repository.JobSeekerApplyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class JobPostActivityService {

    private final JobPostActivityRepository jobPostActivityRepository;
    private final JobSeekerApplyRepository jobSeekerApplyRepository;

    public JobPostActivityService(JobPostActivityRepository jobPostActivityRepository, JobSeekerApplyRepository jobSeekerApplyRepository) {
        this.jobPostActivityRepository = jobPostActivityRepository;
        this.jobSeekerApplyRepository  = jobSeekerApplyRepository;
    }

    public JobPostActivity addNew(JobPostActivity jobPostActivity) {
        return jobPostActivityRepository.save(jobPostActivity);
    }

    public Page<RecruiterJobsDto> getRecruiterJobs(int recruiter, Pageable pageable) {
        Page<IRecruiterJobs> recruiterJobsDtos = jobPostActivityRepository.getRecruiterJobs(recruiter, pageable);
        return recruiterJobsDtos.map(rec -> {
            JobLocation loc = new JobLocation(rec.getLocationId(), rec.getCity(), rec.getState(), rec.getCountry());
            JobCompany comp = new JobCompany(rec.getCompanyId(), rec.getName(), "");
            return new RecruiterJobsDto(rec.getTotalCandidates(), rec.getJob_post_id(),
                    rec.getJob_title(), loc, comp);
        });
    }

    public JobPostActivity getOne(int id) {
        return jobPostActivityRepository.findById(id).orElseThrow(()->new RuntimeException("Job not found"));
    }

    public Page<JobPostActivity> getAll(Pageable pageable) {
        return jobPostActivityRepository.findAll(pageable);
    }

    public Page<JobPostActivity> search(String job, String location, List<String> type, List<String> remote, LocalDate searchDate, Pageable pageable) {
        return Objects.isNull(searchDate) ? jobPostActivityRepository.searchWithoutDate(job, location, remote,type, pageable) :
                jobPostActivityRepository.search(job, location, remote, type, searchDate, pageable);
    }

    public void deleteById(int id) {
        jobPostActivityRepository.deleteById(id);
    }

    public List<JobPostActivity> getRecommendedJobs(List<String> skillNames) {
        List<JobPostActivity> recommendedJobs = new java.util.ArrayList<>();
        for (String skill : skillNames) {
            recommendedJobs.addAll(jobPostActivityRepository.findBySkill(skill));
        }
        return recommendedJobs.stream().distinct().collect(java.util.stream.Collectors.toList());
    }
    



}
