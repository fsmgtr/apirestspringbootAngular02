package br.com.project.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import br.com.project.ApplicationContextLoad;
import br.com.project.model.Usuario;
import br.com.project.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	// Tempo de validade do TOKEN aqui abaixo está dois dias
	private static final long EXPIRATION_TIME = 172800000;

	// Senha única para compor a autenticação e ajudar na segurança
	private static final String SECRET = "SenhaExtremamenteSecreta";

	// prefixo padrão de token
	private static final String TOKEN_PREFIX = "Bearer";

	// prefixo que será retornar para a resposta ou cabeçalho
	private static final String HEADER_STRING = "Authorization";

	// Gerando token de autenticação e adicionando ao cabeçado e resposta http

	public void addAuthentication(HttpServletResponse response, String username) throws IOException {

		// Montagem do TOKEN
		String JWT = Jwts.builder() // chamada o gerador de TOKEN
				.setSubject(username)// Add o usuário que está tentando fazer o login
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))// Tempo de inspiração
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();// Compactação e Algoritmo de geração de senha

		// Junta o token com o prefixo
		String token = TOKEN_PREFIX + " " + JWT;

		// Adiciona no cabeçalho HTTP
		response.addHeader(HEADER_STRING, token);

		// Adiciona a resposta no corpo Http também
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
	}

	// Retorna o usuário validado com o TOKEN ou caso não seja válido retorna NULL
	public Authentication getAuthentication(HttpServletRequest request) {
		// Pega o TOKEN enviado no cabeçalho HTTP
		String token = request.getHeader(HEADER_STRING);

		if (token != null) {
			// faz a validação do TOKEN do usuário na requisição
			String user = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody()
					.getSubject();

			if (user != null) {
				Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
						.findUserByLogin(user);// retornar o usuário logado

				if (usuario != null) {
					return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(),
							usuario.getAuthorities());
				}
			}
		}
		return null; // Não autorizado
	}

}
