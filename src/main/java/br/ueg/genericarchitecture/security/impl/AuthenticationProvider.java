package br.ueg.genericarchitecture.security.impl;

import br.ueg.genericarchitecture.dto.CredentialDTO;
import br.ueg.genericarchitecture.exception.SecurityException;
import br.ueg.genericarchitecture.security.Credential;
import br.ueg.genericarchitecture.security.IAuthenticationProvider;
import br.ueg.genericarchitecture.service.impl.AuthService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationProvider implements IAuthenticationProvider {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private AuthService authService;

    @Override
    public Credential getAuthentication(final String accessToken) {
        CredentialDTO credentialDTO;

        try {
            credentialDTO = authService.getInfoByToken(accessToken);
        } catch (SecurityException e) {
            logger.error("Acesso negado.", e);
            throw e;
        }
        return credentialDTO;
    }
}
