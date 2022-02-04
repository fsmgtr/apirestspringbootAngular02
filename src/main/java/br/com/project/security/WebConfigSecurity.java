package br.com.project.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import br.com.project.service.ImplementacaoUserDetailsService;

/*Mapeia URLS, endereços, autoriza ou bloqueia acessos a urls*/
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	// Configura as solicitações de acesso por http
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Ativando a proteção contra usuários que não estão validados por token
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		//Ativando a restrição a URL
		.disable().authorizeRequests().antMatchers("/").permitAll()
		.antMatchers("/index").permitAll()
		//URL de logout - redireciona após o user deslogar do sistema
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
		//mapeia a url de logout e invalida o usuário
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		//Filtra requisições de login para autenticação
		.and().addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
		//filtra demais requisições para verificar a presença do TOKEN JWT no header http
		.addFilterBefore(new JWTAPIAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		// Service que irá consultar o usiário no banco de dados
		auth.userDetailsService(implementacaoUserDetailsService)
				// Padrão de codificação de senha
				.passwordEncoder(new BCryptPasswordEncoder());

	}

}
