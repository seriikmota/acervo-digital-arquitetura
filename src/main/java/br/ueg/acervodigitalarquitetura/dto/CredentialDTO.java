package br.ueg.acervodigitalarquitetura.dto;

import br.ueg.acervodigitalarquitetura.security.Credential;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CredentialDTO implements Credential {
    private Long id;
    private String name;
    private String login;
    private String email;
    private List<String> roles;
    private String accessToken;
    private Long expiresIn;
    private String refreshToken;
    private Long refreshExpiresIn;
    private boolean activeState;
    private String password;
}
