package br.ueg.genericarchitecture.controller.impl;

import br.ueg.genericarchitecture.dto.CredentialDTO;
import br.ueg.genericarchitecture.security.impl.CredentialProvider;

public abstract class AbstractController {

    protected CredentialDTO getCredential() {
        return (CredentialDTO) CredentialProvider.newInstance().getCurrentInstance();
    }

    protected Long getIdFromLoggedUser() {
        CredentialDTO credential = getCredential();
        return credential != null ? credential.getId() : null;
    }

    protected String getUserNameFromLoggedUser() {
        CredentialDTO credential = getCredential();
        return credential != null ? credential.getLogin() : null;
    }
}
