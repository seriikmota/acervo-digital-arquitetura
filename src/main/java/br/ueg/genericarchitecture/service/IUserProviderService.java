package br.ueg.genericarchitecture.service;

import br.ueg.genericarchitecture.dto.CredentialDTO;

public interface IUserProviderService {
    CredentialDTO getCredentialByLogin(String username);
    CredentialDTO getCredentialByEmail(String email);
    void recordLog(CredentialDTO credentialDTO, String action);
}
