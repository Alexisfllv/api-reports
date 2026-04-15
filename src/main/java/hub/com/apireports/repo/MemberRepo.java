package hub.com.apireports.repo;

import hub.com.apireports.entity.security.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// #3
public interface MemberRepo extends JpaRepository<Member,Long> {
    Optional<Member> findByUsername(String username);

    // scan index emai,phone,dni
    Optional<Member> findByEmailOrPhoneOrDni(String email, String phone, String dni);
}

