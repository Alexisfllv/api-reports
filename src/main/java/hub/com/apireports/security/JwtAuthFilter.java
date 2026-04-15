package hub.com.apireports.security;


import hub.com.apireports.service.security.MemberDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// #6
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberDetailServiceImpl memberDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Leer header Authorization
        String authHeader = request.getHeader("Authorization");

        // Si no tiene token , continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer el token (quitar "Bearer ")
        String token = authHeader.substring(7);

        // Extraer username del token
        String username = jwtUtil.extractUsername(token);

        // Si hay username y no esta autenticado aun
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Buscar usuario en BD
            UserDetails userDetails = memberDetailService.loadUserByUsername(username);

            // Validar token
            if (jwtUtil.isTokenValid(token, userDetails)) {

                // Crear autentcacion y registrar en el contexto
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuar con el siguiente fitro
        filterChain.doFilter(request, response);
    }
}
