package com.royasoftware.settings.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;

import com.royasoftware.model.Account;
import com.royasoftware.model.Role;

public class CustomUserDetails extends User implements UserDetails {

    private static final long serialVersionUID = 1L;
    Long id;
    

	public CustomUserDetails(Account account, Collection<GrantedAuthority> grantedAuthorities) {
//        super(user);
        super(account.getUsername(),
                account.getPassword(), account.isEnabled(),
                !account.isExpired(), !account.isCredentialsexpired(),
                !account.isLocked(), grantedAuthorities);
        setId(account.getId());
    }



	public Long getId() {
		return id;
	}


	public void setId(Long pk) {
		this.id = pk;
	}


    @Override
    public Collection<GrantedAuthority> getAuthorities() {
//        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
//        for (Role role : getRoles()) {
//            grantedAuthorities.add(new SimpleGrantedAuthority(role.getCode()));
//        }
        return super.getAuthorities();
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

//    @Override
//    public Set<GrantedAuthority> getAuthorities() {
//        return super.getAuthorities();
//    }
}