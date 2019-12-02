package com.ibagroup.collectme.service.mapper;

import com.ibagroup.collectme.domain.Authority;
import com.ibagroup.collectme.domain.User;
import com.ibagroup.collectme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;


public class LDAPUserDetailsContextMapper implements UserDetailsContextMapper {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> clctn) {
        Optional<User> isUser = userRepository.findOneWithAuthoritiesByLogin(username);
        final User user = isUser.get();
        Set<Authority> userAuthorities = user.getAuthorities();
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Authority a : userAuthorities) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
                a.getName());
            grantedAuthorities.add(grantedAuthority);
        }
        return new org.springframework.security.core.userdetails.User(
            username, "1", grantedAuthorities);
    }

    @Override
    public void mapUserToContext(UserDetails ud, DirContextAdapter dca) {

    }
}
