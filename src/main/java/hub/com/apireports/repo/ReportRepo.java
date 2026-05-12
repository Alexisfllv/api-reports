package hub.com.apireports.repo;

import hub.com.apireports.entity.Report;
import hub.com.apireports.entity.enums.PriorityLevel;
import hub.com.apireports.entity.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepo extends JpaRepository<Report,Long> {


    // Summary

    @Query("SELECT DISTINCT  r FROM Report r LEFT JOIN FETCH r.files")
    List<Report> findallWithFiles();

    // get all reports by id member
    List<Report> findByMemberId(Long memberId);

    // dashboard
    Long countByStatus(ReportStatus status);

    Long countByPriorityLevel(PriorityLevel priorityLevel);


    // criticalToday
    Long countByPriorityLevelAndReportDateBetween(
            PriorityLevel priorityLevel,
            LocalDateTime start,
            LocalDateTime end
    );

    // pendingToday
    Long countByStatusAndReportDateBetween(
            ReportStatus status,
            LocalDateTime start,
            LocalDateTime end
    );


    // createdToday
    Long countByReportDateBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}
