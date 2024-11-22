package br.ueg.genericarchitecture.service.impl;

import br.ueg.genericarchitecture.config.Constants;
import br.ueg.genericarchitecture.security.impl.TokenBuilder;
import com.auth0.jwt.interfaces.Claim;

import java.util.Map;

public class AuthClaimResolve {

    private final Map<String, Claim> claims;

    private AuthClaimResolve(final Map<String, Claim> claims) {
        this.claims = claims;
    }

    public static AuthClaimResolve newInstance(final Map<String, Claim> claims) {
        return new AuthClaimResolve(claims);
    }

    public String getLogin() {
        Claim claim = claims.get(Constants.PARAM_LOGIN);
        return claim != null && !claim.isNull() ? claim.asString() : null;
    }

    public String getEmail() {
        Claim claim = claims.get(Constants.PARAM_EMAIL);
        return claim != null && !claim.isNull() ? claim.asString() : null;
    }

    public String getName() {
        Claim claim = claims.get(Constants.PARAM_NAME);
        return claim != null && !claim.isNull() ? claim.asString() : null;
    }

    public Long getExpiresIn() {
        Claim claim = claims.get(Constants.PARAM_EXPIRES_IN);
        return claim != null && !claim.isNull() ? claim.asLong() : null;
    }

    public Long getRefreshExpiresIn() {
        Claim claim = claims.get(Constants.PARAM_REFRESH_EXPIRES_IN);
        return claim != null && !claim.isNull() ? claim.asLong() : null;
    }

    public Long getUserId() {
        Claim claim = claims.get(Constants.PARAM_USER_ID);
        return claim != null && !claim.isNull() ? claim.asLong() : null;
    }

    public TokenBuilder.TokenType getTokenType() {
        Claim claim = claims.get(Constants.PARAM_TYPE);
        return claim != null && !claim.isNull() ? TokenBuilder.TokenType.valueOf(claim.asString()) : null;
    }

    public boolean isTokenTypeAccess() {
        TokenBuilder.TokenType type = getTokenType();
        return TokenBuilder.TokenType.ACCESS.equals(type);
    }

    public boolean isTokenTypeRefresh() {
        TokenBuilder.TokenType type = getTokenType();
        return TokenBuilder.TokenType.REFRESH.equals(type);
    }
}
