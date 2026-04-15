package hub.com.apireports.controller;

import hub.com.apireports.dto.security.MemberDTOResponse;
import hub.com.apireports.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/find-all")
    ResponseEntity<List<MemberDTOResponse>> findAllGet(){
        List<MemberDTOResponse> list = memberService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/find-all-index")
    ResponseEntity<List<MemberDTOResponse>> findAllIndexGet(
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String dni
    ){
        List<MemberDTOResponse> list = memberService.findByIndex(email, phone, dni);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

}
