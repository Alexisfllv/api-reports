package hub.com.apireports.service.security;



import hub.com.apireports.repo.MemberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
// #4

@Service
@RequiredArgsConstructor
public class MemberDetailServiceImpl implements UserDetailsService {

    private final MemberRepo memberRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found : "+username));
    }
}
