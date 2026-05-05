package hub.com.apireports.repo;

import hub.com.apireports.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportRepo extends JpaRepository<Report,Long> {


    // Summary

    @Query("SELECT DISTINCT  r FROM Report r LEFT JOIN FETCH r.files")
    List<Report> findallWithFiles();
}
