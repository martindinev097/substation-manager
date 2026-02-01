package com.buildingenergy.substation_manager.config;

import com.buildingenergy.substation_manager.login.handler.LoginFailureHandler;
import com.buildingenergy.substation_manager.login.handler.LoginSuccessHandler;
import com.buildingenergy.substation_manager.security.AccountStatusFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity
public class WebConfiguration implements WebMvcConfigurer {

    private final AccountStatusFilter accountStatusFilter;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;

    public WebConfiguration(AccountStatusFilter accountStatusFilter, LoginSuccessHandler loginSuccessHandler, LoginFailureHandler loginFailureHandler) {
        this.accountStatusFilter = accountStatusFilter;
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailureHandler = loginFailureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.addFilterAfter(accountStatusFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.authorizeHttpRequests(matcher -> matcher
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/", "/login", "/register").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessHandler(((request, response, authentication) -> {
                            if ("true".equals(request.getParameter("roleChanged"))) {
                                response.sendRedirect("/login?roleChanged=true");
                            } else {
                                response.sendRedirect("/login?logout");
                            }
                        }))
                );

        return httpSecurity.build();
    }

}
