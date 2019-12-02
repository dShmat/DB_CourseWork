package com.ibagroup.collectme.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomAuthenticationManager extends ProviderManager implements AuthenticationManager {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationManager.class);

    public CustomAuthenticationManager(List<AuthenticationProvider> providers) {
        super(providers);
        providers.forEach(e->log.debug("Registered providers "+e.getClass().getName()));
    }

}

