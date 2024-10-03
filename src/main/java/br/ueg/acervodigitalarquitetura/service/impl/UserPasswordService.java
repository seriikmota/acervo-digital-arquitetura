package br.ueg.acervodigitalarquitetura.service.impl;

import br.ueg.acervodigitalarquitetura.dto.AuthDTO;
import br.ueg.acervodigitalarquitetura.dto.CredentialDTO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserPasswordService {
    public static Boolean loginByPassword(AuthDTO authDTO, CredentialDTO userCredential) {
        if (!authDTO.getLogin().equals(userCredential.getLogin())) {
            return false;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(authDTO.getPassword(), userCredential.getPassword());
    }
}
