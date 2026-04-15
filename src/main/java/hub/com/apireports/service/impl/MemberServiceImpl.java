package hub.com.apireports.service.impl;


import hub.com.apireports.dto.security.MemberDTOResponse;
import hub.com.apireports.mapper.MemberMapper;
import hub.com.apireports.repo.MemberRepo;
import hub.com.apireports.service.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
