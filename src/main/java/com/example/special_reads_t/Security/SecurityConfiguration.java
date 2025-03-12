package com.example.special_reads_t.Security;


import com.example.special_reads_t.Security.jwt.JwtRequestFilter;
import com.example.special_reads_t.Security.jwt.UnauthorizedHandlerJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    public RepositoryUserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

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
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/chat/**").permitAll()
                        .requestMatchers("/api/book/**").permitAll()
                        .anyRequest().permitAll()
                );

        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Disable Basic Authentication
        http.httpBasic(httpBasic -> httpBasic.disable());

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT Token filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/css/**","/fonts/**","/images/**","/js/**","/scss/**", "/docs/**", "/fonts.Ubuntu/**", "/glup-tasks/**", "/pages/**", "/vendors/**", "/assets/**", "/static/**").permitAll()
                        // PUBLIC PAGES
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/signUp").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/loginerror").permitAll()
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
                        .requestMatchers("/book/search").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/apiResponse").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/detailsBook/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/journal/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/review/**").hasAnyRole("USER", "ADMIN")

                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .failureUrl("/loginerror")
                        .defaultSuccessUrl("/indexUser", true)
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
}
