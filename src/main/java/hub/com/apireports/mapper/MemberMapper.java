package hub.com.apireports.mapper;


import hub.com.apireports.dto.security.MemberDTOResponse;
import hub.com.apireports.entity.security.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public MemberDTOResponse toMemberDTOResponse(Member member){
        return new MemberDTOResponse(
                member.getId(),
                member.getName(),
                member.getLastName(),
                member.getDni(),
                member.getPhone(),
                member.getEmail(),
                member.getUsername(),
                member.getRole(),
                member.getStatus()
        );
    }
}
