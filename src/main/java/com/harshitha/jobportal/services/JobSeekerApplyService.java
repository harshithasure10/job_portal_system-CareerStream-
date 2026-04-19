package com.harshitha.jobportal.services;

import com.harshitha.jobportal.entity.JobPostActivity;
import com.harshitha.jobportal.entity.JobSeekerApply;
import com.harshitha.jobportal.entity.JobSeekerProfile;
import com.harshitha.jobportal.repository.JobSeekerApplyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSeekerApplyService {

    private final JobSeekerApplyRepository jobSeekerApplyRepository;

    public JobSeekerApplyService(JobSeekerApplyRepository jobSeekerApplyRepository) {
        this.jobSeekerApplyRepository = jobSeekerApplyRepository;
    }

    public List<JobSeekerApply> getCandidatesJobs(JobSeekerProfile userAccountId) {
        return jobSeekerApplyRepository.findByUserId(userAccountId);
    }

    public List<JobSeekerApply> getJobCandidates(JobPostActivity job) {
        return jobSeekerApplyRepository.findByJob(job);
    }

    public void addNew(JobSeekerApply jobSeekerApply) {
        jobSeekerApplyRepository.save(jobSeekerApply);
    }

    public java.util.Optional<JobSeekerApply> getOne(Integer id) {
        return jobSeekerApplyRepository.findById(id);
    }
}
