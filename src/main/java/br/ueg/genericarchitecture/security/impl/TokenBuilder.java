package br.ueg.genericarchitecture.security.impl;

import br.ueg.genericarchitecture.config.Constants;
import br.ueg.genericarchitecture.enums.ApiErrorEnum;
import br.ueg.genericarchitecture.exception.BusinessException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.util.Strings;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TokenBuilder {
    private final Log logger = LogFactory.getLog(getClass());
    private final JWTCreator.Builder builder;
    private final KeyToken keyToken;

    public enum TokenType {ACCESS, REFRESH}

    public TokenBuilder(KeyToken keyToken) {
        this.keyToken = keyToken;
        this.builder = JWT.create()
                .withIssuedAt(new Date())
                .withIssuer(keyToken.getIssuer());

    }

    public TokenBuilder(String secretKey, String issuer) {
        this(new KeyToken(secretKey, issuer));
    }

    public TokenBuilder addParam(String name, String value) {
        builder.withClaim(name, value);
        return this;
    }

    public TokenBuilder addParam(String name, Long value) {
        builder.withClaim(name, value);
        return this;
    }

    public TokenBuilder addLogin(String login) {
        builder.withClaim(Constants.PARAM_LOGIN, login);
        return this;
    }

    public TokenBuilder addRoles(List<String> roles) {
        builder.withArrayClaim(Constants.PARAM_ROLES, roles.toArray(new String[roles.size()]));
        return this;
    }

    public TokenBuilder addName(final String name) {
        builder.withClaim(Constants.PARAM_NAME, name);
        return this;
    }

    private Date getExpiresAt(Long expiry) {
        LocalDateTime current = LocalDateTime.now().plusSeconds(expiry);
        return Date.from(current.atZone(ZoneId.systemDefault()).toInstant());
    }

    public JWTToken buildAccess(Long expiry) {
        if (expiry == null) throw new IllegalArgumentException("O parâmetro 'expiry' deve ser especificado.");

        Date expiresAt = getExpiresAt(expiry);
        builder.withExpiresAt(expiresAt);

        builder.withClaim(Constants.PARAM_TYPE, TokenType.ACCESS.toString());
        Algorithm algorithm = Algorithm.HMAC256(keyToken.getSecretKey());

        String token = builder.sign(algorithm);
        return new JWTToken(token, expiry);
    }

    public JWTToken buildRefresh(Long expiry) {
        if (expiry == null) throw new IllegalArgumentException("O parâmetro 'expiry' deve ser especificado.");

        Date expiresAt = getExpiresAt(expiry);
        builder.withExpiresAt(expiresAt);

        builder.withClaim(Constants.PARAM_TYPE, TokenType.REFRESH.toString());
        Algorithm algorithm = Algorithm.HMAC256(keyToken.getSecretKey());

        String token = builder.sign(algorithm);
        return new JWTToken(token, expiry);
    }

    public Map<String, Claim> getClaims(final String token) {
        if (Strings.isEmpty(token)) throw new IllegalArgumentException("O parâmetro 'token' deve ser especificado.");

        try {
            Algorithm algorithm = Algorithm.HMAC256(keyToken.getSecretKey());
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(keyToken.getIssuer()).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaims();
        } catch (TokenExpiredException e) {
            logger.warn("Token expirado!");
            throw new BusinessException(ApiErrorEnum.EXPIRED_TOKEN);
        } catch (JWTVerificationException e) {
            logger.warn("Token Invalido!", e);
            throw new BusinessException(ApiErrorEnum.INVALID_TOKEN);
        }
    }

    @Data
    public static class JWTToken implements Serializable {
        private String token;
        private Long expiresIn;

        public JWTToken(final String token, final Long expiresIn) {
            this.token = token;
            this.expiresIn = expiresIn;
        }
    }
}
