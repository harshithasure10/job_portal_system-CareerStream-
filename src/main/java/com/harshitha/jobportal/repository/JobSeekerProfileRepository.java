package com.harshitha.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.harshitha.jobportal.entity.JobSeekerProfile;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, Integer> {
}
