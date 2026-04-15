package hub.com.apireports.service;

import hub.com.apireports.dto.security.MemberDTOResponse;

import java.util.List;

public interface MemberService {

    // GET
    // findAll
    List<MemberDTOResponse> findAll();
}
