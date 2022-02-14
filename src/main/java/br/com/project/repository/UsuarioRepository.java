package br.com.project.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.project.model.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
	
	@Query("SELECT u from Usuario u WHERE u.login = ?1")
	Usuario findUserByLogin(String login);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value="update usuario set token = ?1 WHERE login = ?2")
	void atualizaTokenUsuario(String token, String login);
}
