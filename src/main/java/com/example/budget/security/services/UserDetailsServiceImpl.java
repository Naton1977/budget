package com.example.budget.security.services;

import com.example.budget.domain.entity.User;
import com.example.budget.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findFamilyMemberByMemberLogin(username));
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }
        User user = userOptional.get();
        return UserDetailsImpl.build(user);
    }

}