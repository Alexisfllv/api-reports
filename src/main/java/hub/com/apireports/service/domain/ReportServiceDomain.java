package hub.com.apireports.service.domain;

import hub.com.apireports.entity.Report;
import hub.com.apireports.repo.ReportRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportServiceDomain {

    private final ReportRepo reportRepo;

    public Report findById(Long id){
            return  reportRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Report not found :"+id));
    }
}
