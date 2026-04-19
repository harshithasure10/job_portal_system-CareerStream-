package com.harshitha.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.harshitha.jobportal.entity.JobPostActivity;
import com.harshitha.jobportal.entity.JobSeekerProfile;
import com.harshitha.jobportal.entity.JobSeekerSave;

import java.util.List;

@Repository
public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {

    public List<JobSeekerSave> findByUserId(JobSeekerProfile userAccountId);

    List<JobSeekerSave> findByJob(JobPostActivity job);

}
