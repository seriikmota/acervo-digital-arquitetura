package br.ueg.acervodigitalarquitetura.security.impl;

import br.ueg.acervodigitalarquitetura.security.Credential;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(getClass());
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private final String urlAuthController;
    private final AuthenticationProvider authenticationProvider;

    public JwtAuthenticationFilter(AuthenticationProvider authenticationProvider, String urlAuthController) {
        this.authenticationProvider = authenticationProvider;
        this.urlAuthController = urlAuthController;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
        if(!servletRequest.getRequestURI().contains(this.urlAuthController)) {
            final String token = getAccessToken(servletRequest);

            if (!Strings.isEmpty(token) && isTokenBearer(servletRequest)) {
                Credential credential = authenticationProvider.getAuthentication(token);
                Authentication authentication = getAuthentication(credential);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(servletRequest, servletResponse);
    }

    private Authentication getAuthentication(final Credential credential) {
        Authentication authentication;

        if (credential == null) {
            authentication = new UsernamePasswordAuthenticationToken(null, null);
        } else {
            List<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(credential);
            authentication = new UsernamePasswordAuthenticationToken(credential.getLogin(), credential, grantedAuthorities);
        }
        return authentication;
    }

    private List<GrantedAuthority> getGrantedAuthorities(final Credential credential) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        if (!CollectionUtils.isEmpty(credential.getRoles())) {
            credential.getRoles().forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role)));
        }
        return grantedAuthorities;
    }

    private String getAccessToken(final HttpServletRequest request) {
        String accessToken = null;

        if (request != null) {
            accessToken = request.getHeader(AUTH_HEADER);
            if (!Strings.isEmpty(accessToken)) {
                accessToken = accessToken.replaceAll(BEARER_PREFIX, "").trim();
            }
        }
        return accessToken;
    }

    private boolean isTokenBearer(final HttpServletRequest request) {
        boolean valid = false;
        if (request != null) {
            String accessToken = request.getHeader(AUTH_HEADER);
            valid = !Strings.isEmpty(accessToken) && accessToken.trim().startsWith(BEARER_PREFIX);
        }
        return valid;
    }
}
