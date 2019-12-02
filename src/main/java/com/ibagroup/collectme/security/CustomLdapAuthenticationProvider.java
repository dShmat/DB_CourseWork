package com.ibagroup.collectme.security;

import com.ibagroup.collectme.config.ApplicationProperties;
import com.ibagroup.collectme.service.util.LdapContextSourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Component
public class CustomLdapAuthenticationProvider implements AuthenticationProvider {


    private static final Logger log = LoggerFactory.getLogger(CustomLdapAuthenticationProvider.class);

    private final ApplicationProperties applicationProperties;

    private LdapAuthenticationProvider provider;

    public CustomLdapAuthenticationProvider(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {


        LdapContextSourceUtil ldapContextSourceUtil = new LdapContextSourceUtil(applicationProperties);
        LdapContextSource ldapContextSource = ldapContextSourceUtil.getContextSource();

        BindAuthenticator bindAuth = new BindAuthenticator(ldapContextSource);
        FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch(
            "", "(mail=" + authentication.getName().toLowerCase() + ")",
            ldapContextSource);
        try {
            bindAuth.setUserSearch(userSearch);
            bindAuth.afterPropertiesSet();
        } catch (Exception ex) {
            log.debug("Exception in Ldap binding: ", ex);
        }

        provider = new LdapAuthenticationProvider(bindAuth);
        provider.setUserDetailsContextMapper(new LdapUserDetailsMapper());
        try {
            return provider.authenticate(authentication);
        } catch (Exception ex) {
            log.debug("Could not connect to Ldap with exception: ", ex);
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
