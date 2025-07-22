package com.veersa.eventApp.security;


import com.veersa.eventApp.model.Role;
import com.veersa.eventApp.model.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.stream.Collectors;




public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }


    public Long getId() {
        return user.getId();
    }

    // Returns the authorities granted to the user.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().getName()));
    }


    // Returns the password used to authenticate the user
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    //    Returns the username used to authenticate the user.
    @Override
    public String getUsername() {
        return user.getEmail(); // Assuming email is used as the username
    }


    // Indicates whether the user's account has expired. An expired account cannot be authenticated.
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }



    // Indicates whether the user is locked or unlocked. A locked user cannot be authenticated.
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }


    //  Indicates whether the user's credentials (password) has expired. Expired
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    //  Indicates whether the user is enabled or disabled. A disabled user cannot be authenticated.
    @Override
    public boolean isEnabled() {
        return true; // Assuming the user is always enabled for simplicity
    }
}
