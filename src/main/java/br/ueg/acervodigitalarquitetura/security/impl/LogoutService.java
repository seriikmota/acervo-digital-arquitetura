package br.ueg.acervodigitalarquitetura.security.impl;

import br.ueg.acervodigitalarquitetura.config.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class LogoutService implements LogoutHandler {
    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        String authHeader = request.getHeader(Constants.HEADER_AUTHORIZATION);
        if (authHeader == null ||!authHeader.startsWith(Constants.HEADER_AUTHORIZATION_BEARER)) {
            return;
        }
        SecurityContextHolder.clearContext();
    }
}
