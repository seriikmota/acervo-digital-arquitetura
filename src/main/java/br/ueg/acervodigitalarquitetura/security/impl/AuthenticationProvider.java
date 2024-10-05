package br.ueg.acervodigitalarquitetura.security.impl;

import br.ueg.acervodigitalarquitetura.dto.CredentialDTO;
import br.ueg.acervodigitalarquitetura.exception.BusinessException;
import br.ueg.acervodigitalarquitetura.security.Credential;
import br.ueg.acervodigitalarquitetura.security.IAuthenticationProvider;
import br.ueg.acervodigitalarquitetura.service.impl.AuthService;
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
        } catch (BusinessException e) {
            logger.error("Acesso negado.", e);
            throw e;
        }
        return credentialDTO;
    }
}
