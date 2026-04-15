package hub.com.apireports.service.impl;


import hub.com.apireports.dto.security.MemberDTOResponse;
import hub.com.apireports.entity.security.Member;
import hub.com.apireports.mapper.MemberMapper;
import hub.com.apireports.repo.MemberRepo;
import hub.com.apireports.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {


    private final MemberMapper memberMapper;
    private final MemberRepo  memberRepo;

    @Override
    public List<MemberDTOResponse> findAll() {
        return memberRepo.findAll()
                .stream()
                .map(memberMapper::toMemberDTOResponse)
                .toList();
    }

    @Override
    public List<MemberDTOResponse> findByIndex(String email, String phone, String dni) {
        Optional<Member> resultList = memberRepo.findByEmailOrPhoneOrDni(email,phone,dni);
        return resultList.stream()
                .map(memberMapper::toMemberDTOResponse)
                .toList();
    }
}
