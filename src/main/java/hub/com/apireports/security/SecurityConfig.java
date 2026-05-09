package hub.com.apireports.security;


import hub.com.apireports.service.security.MemberDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// #7
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final MemberDetailServiceImpl memberDetailService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()

                        // GET específico por recurso
                        .requestMatchers(HttpMethod.GET, "/api/reports/**")
                        .hasAnyAuthority("SUPERVISOR", "ADMIN","MEMBER")
                        .requestMatchers(HttpMethod.GET, "/api/categories/**")
                        .hasAnyAuthority("ADMIN")  // ← Solo ADMIN para categories
                        .requestMatchers(HttpMethod.GET, "/api/members/**")
                        .hasAnyAuthority("ADMIN")  // ← Solo ADMIN para members

                        // Crear reporte y subir archivos → MEMBER, SUPERVISOR, ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/reports/**")
                        .hasAnyAuthority("MEMBER", "SUPERVISOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/report-file/**")
                        .hasAnyAuthority("MEMBER", "SUPERVISOR", "ADMIN")

                        // Cambiar status → solo SUPERVISOR y ADMIN
                        .requestMatchers(HttpMethod.PATCH, "/api/reports/**")
                        .hasAnyAuthority("SUPERVISOR", "ADMIN")

                        // Categories → solo ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/categories/**")
                        .hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**")
                        .hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**")
                        .hasAnyAuthority("ADMIN")

                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(memberDetailService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
