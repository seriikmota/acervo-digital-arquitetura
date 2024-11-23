package br.ueg.genericarchitecture.security.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyToken {

    @Value("${api.security.jwt.secret:defaultSecretKey}")
    private String secretKey;

    @Value("${api.security.jwt.issuer:ApiLapegeo}")
    private String issuer;

    public KeyToken() {}

    public KeyToken(final String secretKey, final String issuer) {
        this.issuer = issuer;
        this.secretKey = secretKey;
    }

    public byte[] getSecretKey() {
        return secretKey.getBytes();
    }

    public String getIssuer() {
        return issuer;
    }
}
