package br.ueg.genericarchitecture.security.impl;

import br.ueg.genericarchitecture.security.Credential;
import br.ueg.genericarchitecture.security.ICredentialProvider;
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
