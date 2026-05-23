package com.nuvemite.cms.complaints.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class CmsAuthenticationToken extends JwtAuthenticationToken {

    private final CmsUserPrincipal cmsPrincipal;

    public CmsAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities, CmsUserPrincipal principal) {
        super(jwt, authorities, principal.subject());
        this.cmsPrincipal = principal;
    }

    @Override
    public Object getPrincipal() {
        return cmsPrincipal;
    }
}
