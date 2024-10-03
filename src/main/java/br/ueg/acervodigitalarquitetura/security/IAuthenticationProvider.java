package br.ueg.acervodigitalarquitetura.security;

public interface IAuthenticationProvider {
    Credential getAuthentication(final String token);
}
