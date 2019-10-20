

package com.royasoftware.school.settings.security;

import com.royasoftware.school.model.Account;
import com.royasoftware.school.model.Role;
import com.royasoftware.school.repository.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {

//    private final AccountRepository userRepository;

	@Autowired(required = false)
    private AccountRepository userRepository;

//    @Autowired(required = false)
//    public CustomUserDetailsService(AccountRepository userRepository) {
//        this.userRepository = userRepository;
//    }

//    @Autowired(required = false)
//    public CustomUserDetailsService(AccountRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account account = userRepository.findByUsername(username);

        if (account == null) {
            // Not found...
            throw new UsernameNotFoundException(
                    "User " + username + " not found.");
        }

        if (account.getRoles() == null || account.getRoles().isEmpty()) {
            // No Roles assigned to user...
            throw new UsernameNotFoundException("User not authorized.");
        }

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        for (Role role : account.getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getCode()));
        }

//        User userDetails = new User(account.getUsername(),
//                account.getPassword(), account.isEnabled(),
//                !account.isExpired(), !account.isCredentialsexpired(),
//                !account.isLocked(), grantedAuthorities);
        CustomUserDetails userDetails = new CustomUserDetails(account,grantedAuthorities);

        return (User)userDetails;
    }
}
