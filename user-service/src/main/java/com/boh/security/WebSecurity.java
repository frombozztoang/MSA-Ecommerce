package com.boh.security;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.boh.service.UserService;

@Configuration // 다른 빈 보다 우선 등된다.
@EnableWebSecurity // 웹 시큐리티 용도로 사용.
public class WebSecurity extends WebSecurityConfigurerAdapter {
	private Environment env;
	private UserService userService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.env = env;
		this.userService = userService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 권한.
		http.csrf().disable();

//		// /users/** URI에 대해서 모두 허가한다.
//		http.authorizeRequests().antMatchers("/users/**").permitAll();

		// /actuator로 들어오는 모든 요청에 대해 허가한다.
		http.authorizeRequests().antMatchers("/actuator/**").permitAll();
		// /users/** URI에 대해서 모두 허가한다.
		http.authorizeRequests().antMatchers("/**")
				//.hasIpAddress("192.168.0.69")
				.hasIpAddress("172.30.1.43")
				.and()
				.addFilter(getAuthenticationFilter());

		// 프레임으로 나눠진 http에서 정상 구동 되도록 한다.
		http.headers().frameOptions().disable();

//		Using generated security password: 8d21b939-eb78-42c3-8acb-1bbb9d1f5a1f
	}

	private AuthenticationFilter getAuthenticationFilter() throws Exception {
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(), env, userService);
		// authenticationFilter.setAuthenticationManager(authenticationManager());
		return authenticationFilter;
	}

	// 인증 처리를 위한 Configure 메소드
	// pwd(encrpted) == input_pwd(encrpted)
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// select pwd from users email = ?
		// 패스워드 인코딩 
		auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);

	}

}
