package rest_api_warehouse_accounting.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.auth0.jwt.exceptions.JWTVerificationException;
import rest_api_warehouse_accounting.security.PersonDetailsService;
import rest_api_warehouse_accounting.util.JWTUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final PersonDetailsService personDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Если запрос идет на регистрацию, пропускаем без проверки токена
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/api/v1/registration")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Получаем заголовок Authorization
        String authHeader = request.getHeader("Authorization");

        // Проверяем, что заголовок не пуст и начинается с "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Убираем "Bearer "

            if (token.isBlank()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token is empty or not valid");
                return;
            }

            try {
                // Извлекаем имя пользователя из токена
                String username = jwtUtil.extractUsername(token);
                UserDetails userDetails = personDetailsService.loadUserByUsername(username);

                // Если роли загружаются только из JWT, получаем их
                Collection<? extends GrantedAuthority> authorities;
                if (userDetails.getAuthorities().isEmpty()) {
                    String role = jwtUtil.extractRole(token); // Достаем роль из токена
                    authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                } else {
                    authorities = userDetails.getAuthorities(); // Берем роли из UserDetails
                }

                // Если контекст аутентификации пуст, устанавливаем пользователя
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (JWTVerificationException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is not valid");
                return;
            }
        }

        // Передаем запрос дальше в цепочке фильтров
        filterChain.doFilter(request, response);
    }
}
