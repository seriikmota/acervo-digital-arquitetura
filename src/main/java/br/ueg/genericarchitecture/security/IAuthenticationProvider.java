package br.ueg.genericarchitecture.security;

public interface IAuthenticationProvider {
    Credential getAuthentication(final String token);
}
