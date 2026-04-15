package hub.com.apireports.service.impl;


import hub.com.apireports.dto.security.MemberDTOResponse;
import hub.com.apireports.entity.security.Member;
import hub.com.apireports.entity.security.RoleType;
import hub.com.apireports.entity.security.UserStatus;
import hub.com.apireports.mapper.MemberMapper;
import hub.com.apireports.repo.MemberRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepo memberRepo;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberServiceImpl memberService;

    Member member;
    MemberDTOResponse memberDTOResponse;

    @BeforeEach
    void setUp() {
        member = new Member(1L,"Sara","Lopez","70576713","920287650","sarama@gmail.com","SL70576713",null, RoleType.MEMBER, UserStatus.ACTIVE);
        memberDTOResponse = new MemberDTOResponse(1L,"Sara","Lopez","70576713","920287650","sarama@gmail.com","SL70576713", RoleType.MEMBER, UserStatus.ACTIVE);
    }

    @Nested
    class FindAll {
        @Test
        public void findAllMember() {
            // Arrange
            List<Member> listEntity= List.of(member);
            List<MemberDTOResponse> listDTOResponse= List.of(memberDTOResponse);
            when(memberRepo.findAll()).thenReturn(listEntity);
            when(memberMapper.toMemberDTOResponse(member)).thenReturn(memberDTOResponse);

            // Act
            List<MemberDTOResponse> result = memberService.findAll();

            // Assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(1,result.size()),
                    () -> assertEquals(listDTOResponse,result)
            );

            // Verifiy InOrder
            InOrder inOrder= Mockito.inOrder(memberRepo,memberMapper);
            inOrder.verify(memberRepo).findAll();
            inOrder.verify(memberMapper).toMemberDTOResponse(member);
            inOrder.verifyNoMoreInteractions();
        }

        @Test
        public void findAllMemberEmpty() {
            // Arange
            List<Member> emptyList= List.of();
            List<MemberDTOResponse> emptyListDto= List.of();
            when(memberRepo.findAll()).thenReturn(emptyList);

            // Act
            List<MemberDTOResponse> result = memberService.findAll();

            // Assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertTrue(result.isEmpty()),
                    () -> assertEquals(emptyListDto,result)
            );

            // Verify InOrder
            InOrder inOrder= Mockito.inOrder(memberRepo,memberMapper);
            inOrder.verify(memberRepo).findAll();
            inOrder.verifyNoMoreInteractions();
        }

    }


    @Nested
    class FindByIdIndex {
        @Test
        public void findByIndex_shouldReturnMember_whenEmailExists() {
            // Arrange
            String email = "sarama@gmail.com";
            when(memberRepo.findByEmailOrPhoneOrDni(email, null, null)).thenReturn(Optional.of(member));
            when(memberMapper.toMemberDTOResponse(member)).thenReturn(memberDTOResponse);
            // Act
            List<MemberDTOResponse> result = memberService.findByIndex(email, null, null);

            // Assert
            assertAll(
                    () -> assertEquals(1, result.size()),
                    () -> assertEquals(memberDTOResponse, result.get(0))
            );

            // InOrder & Verify
            InOrder inOrder = Mockito.inOrder(memberRepo, memberMapper);
            inOrder.verify(memberRepo).findByEmailOrPhoneOrDni(email, null, null);
            inOrder.verify(memberMapper).toMemberDTOResponse(member);
            inOrder.verifyNoMoreInteractions();
        }

        @Test
        public void findByIndex_shouldReturnMember_whenPhoneExists() {
            // Arrange
            String phone = "920287650";
            when(memberRepo.findByEmailOrPhoneOrDni(null, phone, null)).thenReturn(Optional.of(member));
            when(memberMapper.toMemberDTOResponse(member)).thenReturn(memberDTOResponse);

            // Act
            List<MemberDTOResponse> result = memberService.findByIndex(null, phone, null);

            // Assert
            assertAll(
                    () -> assertEquals(1, result.size()),
                    () -> assertEquals(memberDTOResponse, result.get(0))
            );

            // InOrder & Verify
            InOrder inOrder = Mockito.inOrder(memberRepo, memberMapper);
            inOrder.verify(memberRepo).findByEmailOrPhoneOrDni(null, phone, null);
            inOrder.verify(memberMapper).toMemberDTOResponse(member);
            inOrder.verifyNoMoreInteractions();
        }

        @Test
        public void findByIndex_shouldReturnMember_whenDniExists() {
            // Arrange
            String dni = "70576713";
            when(memberRepo.findByEmailOrPhoneOrDni(null, null, dni)).thenReturn(Optional.of(member));
            when(memberMapper.toMemberDTOResponse(member)).thenReturn(memberDTOResponse);

            // Act
            List<MemberDTOResponse> result = memberService.findByIndex(null, null, dni);

            // Assert
            assertAll(
                    () -> assertEquals(1, result.size()),
                    () -> assertEquals(memberDTOResponse, result.get(0))
            );

            // InOrder & Verify
            InOrder inOrder = Mockito.inOrder(memberRepo, memberMapper);
            inOrder.verify(memberRepo).findByEmailOrPhoneOrDni(null, null, dni);
            inOrder.verify(memberMapper).toMemberDTOResponse(member);
            inOrder.verifyNoMoreInteractions();
        }

        @Test
        public void findByIndex_shouldReturnEmptyList_whenNotFound() {
            // Arrange
            String dni = "9876543";
            when(memberRepo.findByEmailOrPhoneOrDni(null, null, dni)).thenReturn(Optional.empty());

            // Act
            List<MemberDTOResponse> result = memberService.findByIndex(null, null, dni);

            // Assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertTrue(result.isEmpty())
            );

            // InOrder & Verify
            InOrder inOrder = Mockito.inOrder(memberRepo, memberMapper);
            inOrder.verify(memberRepo).findByEmailOrPhoneOrDni(null, null, dni);
            inOrder.verifyNoMoreInteractions();
        }
    }

}
