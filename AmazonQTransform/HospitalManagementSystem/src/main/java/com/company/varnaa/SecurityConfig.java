package com.company.varnaa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
	@Bean
	InMemoryUserDetailsManager inMemoryAuthManager() throws Exception {
        PasswordEncoder encoder = 
          PasswordEncoderFactories.createDelegatingPasswordEncoder();
		return new InMemoryUserDetailsManager(User.builder().username("rupa").build());
    }

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(requests -> requests
				.requestMatchers("/").permitAll()
				.requestMatchers("/main").permitAll()
				.requestMatchers("/signup").permitAll()
				.requestMatchers("/doctors/**").hasRole("DOCTOR")
				.requestMatchers("/patients/**").hasRole("PATIENT")
				.requestMatchers("/receptionist/**").hasRole("RECEPTIONIST")
				.requestMatchers("/anonymous*").anonymous()
				.requestMatchers("/login*").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin(login -> login
				// .loginPage("/login")
				// .loginProcessingUrl("/perform_login")
				.defaultSuccessUrl("/showPostLogin", false)
				.permitAll()
			)
			.logout(logout -> logout
				.logoutSuccessUrl("/")
				.permitAll()
			);
		return http.build();
	}
	
}
