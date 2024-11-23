package br.ueg.genericarchitecture.service.impl;

import br.ueg.genericarchitecture.config.Constants;
import br.ueg.genericarchitecture.dto.AuthDTO;
import br.ueg.genericarchitecture.dto.CredentialDTO;
import br.ueg.genericarchitecture.enums.ApiErrorEnum;
import br.ueg.genericarchitecture.exception.SecurityException;
import br.ueg.genericarchitecture.security.impl.KeyToken;
import br.ueg.genericarchitecture.security.impl.TokenBuilder;
import br.ueg.genericarchitecture.service.IUserProviderService;
import com.auth0.jwt.interfaces.Claim;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthService {

    @Autowired
    private IUserProviderService userProviderService;

    @Value("${api.security.jwt.token-expire-in:600}")
    private Long tokenExpireIn;

    @Value("${api.security.jwt.token-refresh-in:600}")
    private Long tokenRefreshExpireIn;

    @Autowired
    private KeyToken keyToken;

    public CredentialDTO login(AuthDTO authDTO) {
        return loginAccess(authDTO);
    }

    public void validateLoginByPassword(AuthDTO authDTO, CredentialDTO credential) {
        if (!UserPasswordService.loginByPassword(authDTO, credential)) {
            throw new SecurityException(ApiErrorEnum.USER_PASSWORD_NOT_MATCH);
        }
    }

    public CredentialDTO loginAccess(final AuthDTO authDTO) {
        validateFieldsAuthDTO(authDTO);

        CredentialDTO credential = userProviderService.getCredentialByLogin(authDTO.getLogin());
        validateCredential(credential);
        validateLoginByPassword(authDTO, credential);

        TokenBuilder builder = new TokenBuilder(keyToken);
        builder.addName(credential.getName());
        builder.addLogin(credential.getLogin());
        builder.addParam(Constants.PARAM_EMAIL, credential.getEmail());
        builder.addParam(Constants.PARAM_USER_ID, credential.getId());
        builder.addParam(Constants.PARAM_EXPIRES_IN, tokenExpireIn);
        builder.addParam(Constants.PARAM_REFRESH_EXPIRES_IN, tokenRefreshExpireIn);

        List<String> roles = credential.getRoles();

        TokenBuilder.JWTToken accessToken = builder.buildAccess(tokenExpireIn);
        credential.setExpiresIn(accessToken.getExpiresIn());
        credential.setAccessToken(accessToken.getToken());

        TokenBuilder.JWTToken refreshToken = builder.buildRefresh(tokenRefreshExpireIn);
        credential.setRefreshExpiresIn(refreshToken.getExpiresIn());
        credential.setRefreshToken(refreshToken.getToken());
        credential.setRoles(roles);

        registerCredentialInSecurityContext(credential);
        credential.setPassword(null);

        userProviderService.recordLog(credential, Constants.ACTION_LOGIN);
        return credential;
    }

    public CredentialDTO refresh(final String refreshToken) {
        AuthClaimResolve resolve = getClaimResolve(refreshToken);
        TokenBuilder builder = new TokenBuilder(keyToken);

        if (!resolve.isTokenTypeRefresh())
            throw new SecurityException(ApiErrorEnum.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);

        CredentialDTO credential = userProviderService.getCredentialByLogin(resolve.getLogin());

        List<String> roles = Objects.nonNull(credential) ? credential.getRoles() : new ArrayList<>();

        credential.setName(resolve.getName());
        credential.setEmail(resolve.getEmail());
        credential.setLogin(resolve.getLogin());
        credential.setId(resolve.getUserId());

        if (resolve.getUserId() != null) {
            builder.addName(resolve.getName());
            builder.addLogin(resolve.getLogin());
            builder.addParam(Constants.PARAM_EMAIL, resolve.getEmail());
            builder.addParam(Constants.PARAM_USER_ID, resolve.getUserId());
        }

        Long expiresIn = resolve.getExpiresIn();
        builder.addParam(Constants.PARAM_EXPIRES_IN, expiresIn);

        Long refreshExpiresIn = resolve.getRefreshExpiresIn();
        builder.addParam(Constants.PARAM_REFRESH_EXPIRES_IN, refreshExpiresIn);

        TokenBuilder.JWTToken accessToken = builder.buildAccess(expiresIn);
        credential.setExpiresIn(accessToken.getExpiresIn());
        credential.setAccessToken(accessToken.getToken());

        TokenBuilder.JWTToken newRefreshToken = builder.buildRefresh(refreshExpiresIn);
        credential.setRefreshExpiresIn(newRefreshToken.getExpiresIn());
        credential.setRefreshToken(newRefreshToken.getToken());
        credential.setRoles(roles);
        userProviderService.recordLog(credential, Constants.ACTION_LOGIN_REFRESH);
        return credential;
    }

    public void logout(final String token) {
        CredentialDTO credential = getInfoByToken(token);
        userProviderService.recordLog(credential, Constants.ACTION_LOGOUT);
        SecurityContextHolder.clearContext();
    }

    public CredentialDTO getInfoByToken(final String token) {
        AuthClaimResolve resolve = getClaimResolve(token);

        if (!resolve.isTokenTypeAccess()) throw new SecurityException(ApiErrorEnum.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);

        CredentialDTO credentialDTO = userProviderService.getCredentialByLogin(resolve.getLogin());

        List<String> roles = Objects.nonNull(credentialDTO) ? credentialDTO.getRoles() : new ArrayList<>();

        credentialDTO.setId(resolve.getUserId());
        credentialDTO.setLogin(resolve.getLogin());
        credentialDTO.setEmail(resolve.getEmail());
        credentialDTO.setName(resolve.getName());
        credentialDTO.setRoles(roles);
        credentialDTO.setPassword(null);
        return credentialDTO;
    }

    private void validateFieldsAuthDTO(final AuthDTO authDTO) {
        if (Strings.isEmpty(authDTO.getLogin()) || Strings.isEmpty(authDTO.getPassword())) {
            throw new SecurityException(ApiErrorEnum.LOGIN_INVALID);
        }
    }

    private void validateCredential(CredentialDTO credential) {
        if (credential == null) {
            throw new SecurityException(ApiErrorEnum.USER_PASSWORD_NOT_MATCH);
        }
        if (!credential.isActiveState()) {
            throw new SecurityException(ApiErrorEnum.INACTIVE_USER);
        }
    }

    private void registerCredentialInSecurityContext(CredentialDTO credential) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(credential.getLogin(), credential);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private AuthClaimResolve getClaimResolve(final String token) {
        String value = getAccessToken(token);
        TokenBuilder builder = new TokenBuilder(keyToken);
        Map<String, Claim> claims = builder.getClaims(value);

        return AuthClaimResolve.newInstance(claims);
    }

    private String getAccessToken(final String value) {
        if (!Strings.isEmpty(value)) {
            return value.replaceAll(Constants.HEADER_AUTHORIZATION_BEARER, "").trim();
        }
        return null;
    }
}
