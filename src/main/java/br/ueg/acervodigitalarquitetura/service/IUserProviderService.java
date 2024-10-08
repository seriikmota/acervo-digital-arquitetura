package br.ueg.acervodigitalarquitetura.service;

import br.ueg.acervodigitalarquitetura.dto.CredentialDTO;

public interface IUserProviderService {
    CredentialDTO getCredentialByLogin(String username);
    CredentialDTO getCredentialByEmail(String email);
    void recordLog(CredentialDTO credentialDTO, String action);
}
