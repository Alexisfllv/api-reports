package hub.com.apireports.repo;

import hub.com.apireports.entity.TrackingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingHistoryRepo extends JpaRepository<TrackingHistory,Long> {
}
