package com.harshitha.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.harshitha.jobportal.entity.JobPostActivity;
import com.harshitha.jobportal.entity.JobSeekerApply;
import com.harshitha.jobportal.entity.JobSeekerProfile;

import java.util.List;

@Repository
public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {

    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findByJob(JobPostActivity job);
}
