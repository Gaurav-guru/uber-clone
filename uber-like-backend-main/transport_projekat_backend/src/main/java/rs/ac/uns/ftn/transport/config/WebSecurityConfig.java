package rs.ac.uns.ftn.transport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import rs.ac.uns.ftn.transport.auth.RestAuthenticationEntryPoint;
import rs.ac.uns.ftn.transport.auth.TokenAuthenticationFilter;
import rs.ac.uns.ftn.transport.service.interfaces.IUserService;
import rs.ac.uns.ftn.transport.util.TokenUtils;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

	private final IUserService userService;
	private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
	private final TokenUtils tokenUtils;

	public WebSecurityConfig(IUserService userService, RestAuthenticationEntryPoint restAuthenticationEntryPoint,
							 TokenUtils tokenUtils){
		this.tokenUtils = tokenUtils;
		this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
		this.userService = userService;
	}

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

 	@Bean
 	public DaoAuthenticationProvider authenticationProvider() {
 	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
 	    authProvider.setUserDetailsService(userService);
 	    authProvider.setPasswordEncoder(passwordEncoder());
 	 
 	    return authProvider;
 	}
 
 	
    // Registrujemo authentication manager koji ce da uradi autentifikaciju korisnika za nas
 	@Bean
 	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
 	    return authConfig.getAuthenticationManager();
 	}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint);
    	http.authorizeRequests()
				.antMatchers("/h2-console/**").permitAll()
//				.antMatchers("/**").permitAll()
					.antMatchers("/api/user/login", "/api/unregisteredUser","api/passenger/*/id").permitAll()
				.antMatchers("/api/driver/*/vehicle", "/api/vehicle/*/location", "/api/user/*/resetPassword"
				).permitAll()
				.antMatchers(HttpMethod.GET,"api/user/*/id", "/api/ride").permitAll()
				.antMatchers(HttpMethod.POST, "/api/passenger").permitAll()
				.antMatchers(HttpMethod.GET, "/api/passenger/activate/*").permitAll()

				.antMatchers(HttpMethod.GET, "/api/ride/favorites/passenger/*").hasRole("PASSENGER")
				.antMatchers(HttpMethod.POST, "/api/ride", "/api/ride/favorites").hasRole("PASSENGER")
				.antMatchers(HttpMethod.GET, "/api/ride/passenger/*/active", "/api/ride/favorites"
				,"/api/ride/favorites/*").hasRole("PASSENGER")
				.antMatchers(HttpMethod.DELETE, "/api/ride/favorites/*").hasRole("PASSENGER")
				.antMatchers(HttpMethod.PUT, "/api/passenger/*", "/api/ride/*/withdraw").hasRole("PASSENGER")

				.antMatchers(HttpMethod.GET, "/api/ride/driver/*/active").hasRole("DRIVER")
				.antMatchers(HttpMethod.PUT, "/api/ride/*/start", "/api/ride/*/start", "/api/ride/*/end",
						"/api/ride/*/cancel").hasRole("DRIVER")
				.antMatchers(HttpMethod.GET, "/api/ride/*/passengers").hasRole("DRIVER")
				.antMatchers("/api/driver/*/working-hour").hasRole("DRIVER")

                .antMatchers(HttpMethod.PUT, "/api/ride/*/accept").hasRole("DRIVER")
				.antMatchers(HttpMethod.POST, "/api/diver/*/documents", "/api/driver").hasRole("ADMIN")
				.antMatchers(HttpMethod.GET, "/api/passenger", "/api/driver",
						"/api/diver/*/documents", "/api/panic").hasRole("ADMIN")
				.antMatchers(HttpMethod.PUT, "/api/driver/*").hasRole("ADMIN")
				.antMatchers(HttpMethod.DELETE, "/api/diver/document/*").hasRole("ADMIN")

				.antMatchers(HttpMethod.GET, "/api/passenger/*/ride").hasAnyRole("ADMIN", "PASSENGER")
				.antMatchers( HttpMethod.PUT,"api/driver/working-hour/*",
						"/api/user/*/block", "/api/user/*/unblock").hasAnyRole("ADMIN", "DRIVER")
				.antMatchers(HttpMethod.PUT, "/api/ride/*/panic").hasAnyRole("DRIVER", "PASSENGER")
				.antMatchers(HttpMethod.POST, "/api/review/**").hasRole("PASSENGER")

				.anyRequest().authenticated().and()
				.cors().and()
				.addFilterBefore(new TokenAuthenticationFilter(tokenUtils,  userService), BasicAuthenticationFilter.class);
		http.csrf().disable();
		http.headers().frameOptions().disable();

        http.authenticationProvider(authenticationProvider());
       
        return http.build();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//    	return (web) -> web.ignoring().antMatchers(HttpMethod.POST, "/auth/login","/socket/**")
//
//    			.antMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html", "favicon.ico",
//    			"/**/*.html", "/**/*.css", "/**/*.js","/socket/**");
//
//    }

}
