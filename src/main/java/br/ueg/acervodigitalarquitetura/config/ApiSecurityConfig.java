package br.ueg.acervodigitalarquitetura.config;

import br.ueg.acervodigitalarquitetura.exception.FilterChainExceptionHandler;
import br.ueg.acervodigitalarquitetura.security.impl.AuthenticationProvider;
import br.ueg.acervodigitalarquitetura.security.impl.JwtAuthenticationFilter;
import br.ueg.acervodigitalarquitetura.security.impl.LogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ApiSecurityConfig {

    @Value("${app.api.security.url-auth-controller:/api/v1/auth}")
    private String urlAuthController;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    protected LogoutService logoutHandler;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    protected AuthenticationProvider authenticationProvider;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private ApiWebConfig apiWebConfig;

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private FilterChainExceptionHandler filterChainExceptionHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        List<String> freeAccess = new ArrayList<>(
                Arrays.asList(urlAuthController.concat("/**"),
                        "/v2/api-docs",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/configuration/ui",
                        "/configuration/security",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/swagger-ui.html"));
        freeAccess.addAll(getCustomFreeAccess());
        String[] freeAccessArray = freeAccess.toArray(new String[0]);

        List<String> freeAccessGetList = new ArrayList<>(getCustomFreeAccessGet());
        String[] freeAccessGetArray = freeAccessGetList.toArray(new String[0]);

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers(HttpMethod.GET, freeAccessGetArray).permitAll()
                                .requestMatchers(freeAccessArray).permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(filterChainExceptionHandler, LogoutFilter.class)
                .logout(
                        logout -> logout.logoutUrl(urlAuthController.concat("/logout"))
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
                .cors(cors -> cors.configurationSource(request -> apiWebConfig.getCorsConfiguration()));

        configureHttpSecurity(http);
        return http.build();
    }

    protected abstract void configureHttpSecurity(HttpSecurity http) throws Exception;

    protected List<String> getCustomFreeAccess() {
        return List.of();
    }

    protected List<String> getCustomFreeAccessGet() {
        return List.of();
    }

    protected JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(authenticationProvider, urlAuthController);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
