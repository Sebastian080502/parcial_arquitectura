package com.iglesia;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        System.out.println("\n================ JWT FILTER =================");
        System.out.println("Método: " + request.getMethod());
        System.out.println("Endpoint: " + request.getRequestURI());

        String header = request.getHeader("Authorization");

        if (header == null) {
            System.out.println("❌ No se envió header Authorization");
        } else {
            System.out.println("Authorization header recibido: " + header);
        }

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);
            System.out.println("Token recibido: " + token);

            if (jwtService.isValid(token)) {

                String email = jwtService.getEmail(token);
                String role = jwtService.getRole(token);

                System.out.println("Token válido");
                System.out.println("Email extraído: " + email);
                System.out.println("Role extraído: " + role);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("Usuario autenticado en SecurityContext");
                System.out.println("Authority asignada: ROLE_" + role);

            } else {
                System.out.println("Token inválido");
            }

        } else {
            System.out.println("Header Authorization no contiene Bearer token");
        }

        System.out.println("Continuando filtro...");
        filterChain.doFilter(request, response);

        System.out.println("============== FIN JWT FILTER ==============\n");
    }
}