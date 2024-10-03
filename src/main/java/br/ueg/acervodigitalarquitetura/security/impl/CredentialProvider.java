package br.ueg.acervodigitalarquitetura.security.impl;

import br.ueg.acervodigitalarquitetura.security.Credential;
import br.ueg.acervodigitalarquitetura.security.ICredentialProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CredentialProvider implements ICredentialProvider {

    public static CredentialProvider newInstance() {
        return new CredentialProvider();
    }

    @Override
    public Credential getCurrentInstance() {
        return (Credential) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }
}
