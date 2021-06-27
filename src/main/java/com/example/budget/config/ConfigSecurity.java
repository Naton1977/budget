package com.example.budget.config;

import com.example.budget.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@EnableWebSecurity
@Configuration
public class ConfigSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    public CustomUserDetailsService customUserDetailsService;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests().antMatchers("/api/v1/product/allFamilyList",
                "/api/v1/product/findByFamilyLogin",
                "/api/v1/product/familyAccount",
                "/api/v1/product/putManyGlobalAdmin",
                "/api/v1/product/withdrawManyGlobalAdmin",
                "/api/v1/product/globAdmLimAllFamily",
                "/api/v1/product/globAdmPersLim",
                "/globalAdmin/admin")
                .access("hasRole('ROLE_GLOBAL_ADMIN')");

        http.authorizeRequests().antMatchers("/api/v1/product/applyLimitations").access("hasRole('ROLE_ADMIN')");

        http.authorizeRequests().antMatchers("/api/v1/product/findAllUsers",
                "/api/v1/product/findFamilyAccount",
                "/api/v1/product/putMany",
                "/api/v1/product/withdrawMoney").access("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')");


        http.authorizeRequests().antMatchers("/family").access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_GLOBAL_ADMIN')");

        http.authorizeRequests().antMatchers("/", "/login", "/logout", "/home").permitAll();

        http.formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/family")
                .failureUrl("/login?error=true")
                .usernameParameter("login")
                .passwordParameter("password")
                .and().logout().logoutSuccessUrl("/login?logout=success");
    }
}
