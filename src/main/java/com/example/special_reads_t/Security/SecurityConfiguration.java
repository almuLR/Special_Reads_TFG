package com.example.special_reads_t.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Autowired
    public RepositoryUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**","/fonts/**","/images/**","/js/**","/scss/**", "/docs/**", "/fonts.Ubuntu/**", "/glup-tasks/**", "/pages/**", "/vendors/**").permitAll()
                        // PUBLIC PAGES
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/signUp").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/login").permitAll()
                        // PRIVATE PAGES
                        .requestMatchers("/indexUser").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/bookSearchs").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/bookShelf").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/challenge").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/charts").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/detailsBook").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/editRanking").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/friends").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/help").hasAnyRole("USER")
                        .requestMatchers("/iaReader").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/journal").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/list").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/newRanking").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/profile").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/profileEdit").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/ranking").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/review").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/helpAdmin").hasAnyRole("ADMIN")

                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .failureUrl("/loginerror")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );



        // Disable CSRF at the moment
        //http.csrf(csrf -> csrf.disable());


        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(){

        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("pass"))
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("adminpass"))
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}
