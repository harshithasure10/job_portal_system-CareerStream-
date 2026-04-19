package com.harshitha.jobportal.repository;

import com.harshitha.jobportal.entity.IRecruiterJobs;
import com.harshitha.jobportal.entity.JobPostActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface JobPostActivityRepository extends JpaRepository<JobPostActivity, Integer> {

    @Query(value = " SELECT COUNT(s.user_id) as totalCandidates, j.job_post_id, j.job_title, l.id as locationId, l.city, l.state, l.country, c.id as companyId, c.name " +
            " FROM job_post_activity j " +
            " inner join job_location l on j.job_location_id = l.id " +
            " INNER join job_company c on j.job_company_id = c.id " +
            " left join job_seeker_apply s on s.job = j.job_post_id " +
            " where j.posted_by_id = :recruiter " +
            " GROUP By j.job_post_id",
            countQuery = "SELECT COUNT(j.job_post_id) FROM job_post_activity j WHERE j.posted_by_id = :recruiter",
            nativeQuery = true)
    Page<IRecruiterJobs> getRecruiterJobs(@Param("recruiter") int recruiter, Pageable pageable);

    @Query(value = "SELECT * FROM job_post_activity j INNER JOIN job_location l on j.job_location_id=l.id  WHERE j" +
            ".job_title LIKE CONCAT('%', :job, '%')"
            + " AND (l.city LIKE CONCAT('%', :location, '%')"
            + " OR l.country LIKE CONCAT('%', :location, '%')"
            + " OR l.state LIKE CONCAT('%', :location, '%')) " +
            " AND (j.job_type IN(:type)) " +
            " AND (j.remote IN(:remote)) ", nativeQuery = true)
    Page<JobPostActivity> searchWithoutDate(@Param("job") String job,
                                            @Param("location") String location,
                                            @Param("remote") List<String> remote,
                                            @Param("type") List<String> type,
                                            Pageable pageable);

    @Query(value = "SELECT * FROM job_post_activity j INNER JOIN job_location l on j.job_location_id=l.id  WHERE j" +
            ".job_title LIKE CONCAT('%', :job, '%')"
            + " AND (l.city LIKE CONCAT('%', :location, '%')"
            + " OR l.country LIKE CONCAT('%', :location, '%')"
            + " OR l.state LIKE CONCAT('%', :location, '%')) " +
            " AND (j.job_type IN(:type)) " +
            " AND (j.remote IN(:remote)) " +
            " AND (posted_date >= :date)", nativeQuery = true)
    Page<JobPostActivity> search(@Param("job") String job,
                                 @Param("location") String location,
                                 @Param("remote") List<String> remote,
                                 @Param("type") List<String> type,
                                 @Param("date") LocalDate searchDate,
                                 Pageable pageable);

    @Query(value = "SELECT * FROM job_post_activity j WHERE j.job_title LIKE CONCAT('%', :skill, '%') OR j.description_of_job LIKE CONCAT('%', :skill, '%')", nativeQuery = true)
    List<JobPostActivity> findBySkill(@Param("skill") String skill);
}
