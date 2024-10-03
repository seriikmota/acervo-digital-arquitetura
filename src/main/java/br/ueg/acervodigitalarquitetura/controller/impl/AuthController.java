package br.ueg.acervodigitalarquitetura.controller.impl;

import br.ueg.acervodigitalarquitetura.controller.IAuthController;
import br.ueg.acervodigitalarquitetura.dto.AuthDTO;
import br.ueg.acervodigitalarquitetura.dto.CredentialDTO;
import br.ueg.acervodigitalarquitetura.service.impl.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.version}/auth")
public class AuthController extends AbstractController implements IAuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthDTO authDTO) {
        CredentialDTO credential = authService.login(authDTO);
        return ResponseEntity.ok(credential);
    }

    @GetMapping(path = "/refresh")
    public ResponseEntity<?> refresh(@RequestParam String refreshToken) {
        CredentialDTO credential = authService.refresh(refreshToken);
        return ResponseEntity.ok(credential);
    }
}
