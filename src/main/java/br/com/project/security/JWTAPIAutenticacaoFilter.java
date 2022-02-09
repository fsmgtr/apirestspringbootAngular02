package br.com.project.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

//filtro onde todas as requisições serão capituradas para autenticar
public class JWTAPIAutenticacaoFilter extends GenericFilterBean{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		
		//Estabelece a autentica para a requisição
		Authentication authentication = new JWTTokenAutenticacaoService().getAuthentication
				((HttpServletRequest) request, (HttpServletResponse) response);
		
		//Coloca o processo de autenticação no spring security
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		//Continua o processo
		chain.doFilter(request, response);
		
	}

}
