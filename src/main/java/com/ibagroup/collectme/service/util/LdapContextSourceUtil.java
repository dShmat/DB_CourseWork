package com.ibagroup.collectme.service.util;

import com.ibagroup.collectme.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * Utility class for setting LdapContextSource from ApplicationProperties.
 */


public final class LdapContextSourceUtil {

    @Autowired
    private ApplicationProperties.Ldap ldap;

    public LdapContextSourceUtil(ApplicationProperties applicationProperties) {
        this.ldap = applicationProperties.getLdap();
    }

    public LdapContextSource getContextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldap.getUrl());
        contextSource.setBase(ldap.getBaseSource());
        contextSource.setUserDn(ldap.getBaseDn());
        contextSource.setPassword(ldap.getBasePassword());
        contextSource.afterPropertiesSet(); //needed otherwise you will have a NullPointerException in spring
        return contextSource;
    }
}
