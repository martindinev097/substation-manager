package com.buildingenergy.substation_manager.config;

import com.buildingenergy.substation_manager.exception.UsernameDoesNotExist;
import com.buildingenergy.substation_manager.security.AccountStatusFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
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

    public WebConfiguration(AccountStatusFilter accountStatusFilter) {
        this.accountStatusFilter = accountStatusFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.addFilterAfter(accountStatusFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.authorizeHttpRequests(matcher -> matcher
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/login", "/register").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .failureHandler(((request, response, exception) -> {
                            Throwable cause = exception.getCause();

                            if (cause instanceof DisabledException || exception instanceof DisabledException) {
                                response.sendRedirect("/login?account-inactive");
                            } else if (cause instanceof UsernameDoesNotExist) {
                                response.sendRedirect("/login?username-not-exist");
                            } else {
                                response.sendRedirect("/login?error");
                            }
                        }))
                        .defaultSuccessUrl("/home", true)
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
