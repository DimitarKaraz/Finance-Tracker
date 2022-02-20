package com.example.financetracker.configurations;


import com.example.financetracker.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Resource
    private MyUserDetailsService myUserDetailsService;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/login", "/register").permitAll()
                    .antMatchers("/**").permitAll()//hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                    .antMatchers("/all_users").hasAuthority("ROLE_ADMIN")
                .and()
                .formLogin(form -> form
                    .defaultSuccessUrl("/profile")
                    .failureUrl("/login?error=true"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
//                        .logoutSuccessHandler(logoutSuccessHandler)
                        .invalidateHttpSession(true)
//                        .addLogoutHandler(logoutHandler)
                        .deleteCookies())
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .invalidSessionUrl("/login")
                .and()
                .csrf().disable()
        ;
//        http.authorizeRequests().antMatchers("/").permitAll().and().csrf().disable();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setUserDetailsService(myUserDetailsService);
        return authenticationProvider;
    }



}
