package hub.com.apireports.service.domain;

import hub.com.apireports.entity.security.Member;
import hub.com.apireports.repo.MemberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberServiceDomain {
    private final MemberRepo memberRepo;

    public Member findById(Long id){
        return  memberRepo.findById(id)
                .orElseThrow( () -> new RuntimeException("Member not found :"+id));
    }
}
