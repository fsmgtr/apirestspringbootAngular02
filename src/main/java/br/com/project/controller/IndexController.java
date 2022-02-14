package br.com.project.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import br.com.project.model.Usuario;
import br.com.project.model.UsuarioDTO;
import br.com.project.repository.UsuarioRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete(@PathVariable("id") Long id) {
		usuarioRepository.deleteById(id);
		return "ok";

	}

	@PutMapping(value = "/{iduser}/idvenda/{idvenda}")
	public ResponseEntity<Usuario> upVenda(@PathVariable Long iduser, @PathVariable Long idvenda) {
		// Usuario usuarioSalvo = usuarioRepository.save(usuario);
		return new ResponseEntity("Venda Atualizada", HttpStatus.OK);
	}

	@PostMapping(value = "/")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws Exception {
		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		/*********************Consumindo uma api publica externa*******************/
		URL url = new URL("https://viacep.com.br/ws/"+usuario.getCep()+"/json/");
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String cep ="";
		StringBuilder jsonCep = new StringBuilder();
		
		while((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}
		
		Usuario userAuxiliar = new Gson().fromJson(jsonCep.toString(), Usuario.class);
		
		usuario.setCep(userAuxiliar.getCep());
		usuario.setLogradouro(userAuxiliar.getLogradouro());
		usuario.setComplemento(userAuxiliar.getComplemento());
		usuario.setBairro(userAuxiliar.getBairro());
		usuario.setLocalidade(userAuxiliar.getLocalidade());
		usuario.setUf(userAuxiliar.getUf());
		usuario.setIbge(userAuxiliar.getIbge());
		/****************************************/
		
		String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografada);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@PutMapping(value = "/")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		
		Usuario userTemp = usuarioRepository.findUserByLogin(usuario.getLogin());
		if (!userTemp.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografada);
		}
		
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<UsuarioDTO> listagem(@PathVariable(value = "id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity<>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}
	@GetMapping(value = "v1/{id}")
	public ResponseEntity<Usuario> listagemV1(@PathVariable(value = "id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
	}
	
	@GetMapping(value = "v2/{id}")
	public ResponseEntity<Usuario> listagemV2(@PathVariable(value = "id") Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		return new ResponseEntity<>(usuario.get(), HttpStatus.OK);
	}

	@GetMapping(value = "/usuarios")
	public ResponseEntity<List<Usuario>> listagemTodos() {
		Iterable<Usuario> usuarios = usuarioRepository.findAll();
		return new ResponseEntity<List<Usuario>>((List<Usuario>) usuarios, HttpStatus.OK);
	}
	
	/* Supondo que o carregamento de usuário seja um processo lento e que queremos controlar ele com cache para
	 * agilizar processo*/
	 
	@GetMapping(value = "/usuarios/cache")
	@CacheEvict(value = "CacheList", allEntries = true)//Remove cache não utilizado
	@CachePut("CacheList")//Se tiver mudanças leva pro cache
	public ResponseEntity<List<Usuario>> listagemTodosComCache() throws InterruptedException {
		Iterable<Usuario> usuarios = usuarioRepository.findAll();
		
		//Thread.sleep(6000);//segura o código por 6 segundos simulando um processo lento SIMULAÇÃO
		return new ResponseEntity<List<Usuario>>((List<Usuario>) usuarios, HttpStatus.OK);
		
	}

	@GetMapping(value = "/")
	public ResponseEntity init() {
		return new ResponseEntity("Olá FilipÃO", HttpStatus.OK);
	}

	@GetMapping(value = "/isname")
	public ResponseEntity init(@RequestParam(value = "nome", defaultValue = "FILIXO") String nome) {
		System.out.println("Recebido" + nome);
		return new ResponseEntity("Olá " + nome, HttpStatus.OK);
	}

	@GetMapping(value = "/user")
	public ResponseEntity<Usuario> initial() {
		Usuario u = new Usuario();
		u.setId(1L);
		u.setLogin("ADMINISTRADOR");
		u.setSenha("123456");
		return ResponseEntity.ok(u);
	}

	@GetMapping(value = "/users")
	public ResponseEntity<Usuario> initials() {
		Usuario u = new Usuario();
		u.setId(1L);
		u.setLogin("ADMINISTRADOR");
		u.setSenha("123456");
		Usuario u1 = new Usuario();
		u1.setId(2L);
		u1.setLogin("15321");
		u1.setSenha("123456");
		List<Usuario> usuarios = new ArrayList<Usuario>();
		usuarios.add(u1);
		usuarios.add(u);
		return new ResponseEntity(usuarios, HttpStatus.OK);
	}

}
