package hub.com.apireports.service.domain;

import hub.com.apireports.entity.security.Member;
import hub.com.apireports.entity.security.RoleType;
import hub.com.apireports.entity.security.UserStatus;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MemberServiceDomainTest {

    @Mock
    private MemberRepo memberRepo;

    @InjectMocks
    private MemberServiceDomain memberServiceDomain;

    Member member;

    @BeforeEach
    public void setup(){
        member = new Member(1L,"alex","fawcett","70576713","987654321","alex@gmail.com","af70576713","****", RoleType.MEMBER, UserStatus.ACTIVE);
    }

    @Nested
    class FindById{
        @Test
        public void findByIdSuccess(){
            // Arrange
            Long idExist = 1L;
            when(memberRepo.findById(idExist)).thenReturn(Optional.of(member));
            // Act
            Member result = memberServiceDomain.findById(idExist);

            // Assert
            assertAll(
                    () -> assertEquals(member.getId(), result.getId()),
                    () -> assertEquals(member.getName(), result.getName()),
                    () -> assertEquals(member.getLastName(), result.getLastName()),
                    () -> assertEquals(member.getDni(), result.getDni()),
                    () -> assertEquals(member.getPhone(), result.getPhone()),
                    () -> assertEquals(member.getEmail(), result.getEmail()),
                    () -> assertEquals(member.getUsername(), result.getUsername()),
                    () -> assertEquals(member.getPassword(), result.getPassword()),
                    () -> assertEquals(member.getRole(), result.getRole()),
                    () -> assertEquals(member.getStatus(), result.getStatus())
            );

            // InOrder & Verify
            InOrder inOrder = Mockito.inOrder(memberRepo);
            inOrder.verify(memberRepo).findById(idExist);
            inOrder.verifyNoMoreInteractions();
        }
        @Test
        public void findByIdFail(){
            // Arrange
            Long idNotExist = 99L;
            when(memberRepo.findById(idNotExist)).thenReturn(Optional.empty());
            // Act
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> memberServiceDomain.findById(idNotExist));

            // Assert
            assertEquals("Member not found :"+idNotExist, ex.getMessage());

            // InOrder & Verify
            InOrder inOrder = Mockito.inOrder(memberRepo);
            inOrder.verify(memberRepo).findById(idNotExist);
            inOrder.verifyNoMoreInteractions();
        }
    }
}
