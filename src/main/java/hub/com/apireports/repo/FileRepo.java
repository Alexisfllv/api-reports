package hub.com.apireports.repo;

import hub.com.apireports.entity.ReportFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepo extends JpaRepository<ReportFile,Long> {
}
