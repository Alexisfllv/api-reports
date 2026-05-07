package hub.com.apireports.repo;

import hub.com.apireports.entity.TrackingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackingHistoryRepo extends JpaRepository<TrackingHistory,Long> {

    // find by id report
    List<TrackingHistory> findByReportId(Long reportId);
}
