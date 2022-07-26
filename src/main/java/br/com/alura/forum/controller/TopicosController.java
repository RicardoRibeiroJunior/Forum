package br.com.alura.forum.controller;

import java.net.URI;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.controller.dto.DetalhesTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;

@RestController
@RequestMapping("/topicos")
public class TopicosController {
	
	@Autowired
	TopicoRepository topicoRepository;
	
	@Autowired
	CursoRepository cursoRepository;
	
	/**@GetMapping
	public Page<TopicoDto> listar(@RequestParam(required = false) String nomeCurso, Pageable paginacao){
	
	Obs.: O Spring consegue facilitar a paginação, dispensando a criação da interface Pageable na mão.
	Para isso basta receber o objeto Pageable como parâmetro do método.
	Habilitar o módulo que reconhece do Spring, anotando a classe Fórum Application com @EnableSpringDataWebSupport
	E passando os nomes dos parâmetros em ingles na URL. 
	(http://localhost:8080/topicos?page=0&size=10&sort=id,desc) 
	
	@PageableDefault(sort = id, direction = Direction.DESC, page = 0, size = 10) Pageable paginacao
	
	Podemos informar uma ordenação padrão com a anotação @PageableDefault caso não seja informado ordenação na requisição.
	
	**/
	
	
	/**
	 * 
	 * PAGINAÇÃO SEM AJUDA DO SPRING BOOT
	 public Page<TopicoDto> listar(@RequestParam(required = false) String nomeCurso,
			@RequestParam int pagina,@RequestParam int qtd, @RequestParam String ordenacao){
		
		Pageable paginacao = PageRequest.of(pagina, qtd, Direction.DESC, ordenacao);
	 **/
	
	@GetMapping
	@Cacheable(value = "listaDeTopicos")
	public Page<TopicoDto> listar(@RequestParam(required = false) String nomeCurso,
			@PageableDefault(sort = "id", direction = Direction.DESC, page = 0, size = 10) Pageable paginacao){
		
		if(nomeCurso == null) {
			Page<Topico> topicos = topicoRepository.findAll(paginacao);
			return TopicoDto.converter(topicos);
		}else {
			Page<Topico> topicos = topicoRepository.findByCurso_Nome(nomeCurso, paginacao);
			return TopicoDto.converter(topicos);
		}
	}
	
	@PostMapping
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true)
	public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder) {
				
		Topico topico = form.converter(cursoRepository);
		topicoRepository.save(topico);
		
		/*
		 * Como boas práticas, após cadastrar deve-se retornar o código 201 - created (sucesso e novo recurso criado) na resposta.
		 * Ne código será devolvido no cabeçalho a URI do recurso criado e no corpo a representação do recurso.
		 */
		
		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
		return ResponseEntity.created(uri).body(new TopicoDto(topico));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<DetalhesTopicoDto> detalhar(@PathVariable Long id) {
		
		Optional<Topico> topico = topicoRepository.findById(id);
		if(topico.isPresent()) {
			return ResponseEntity.ok(new DetalhesTopicoDto(topico.get()));
		}
		
		return ResponseEntity.notFound().build();
		
	}
	
	@PutMapping("/{id}")
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true)
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form){
		
		Optional<Topico> optional = topicoRepository.findById(id);
		if(optional.isPresent()) {
			//Ao final desta linha a banco já estará atualizado e não é necessário chamar novamente o topicoRepository
			Topico topico = form.atualizar(id, topicoRepository);
			
			return ResponseEntity.ok(new TopicoDto(topico));
		}
		
		return ResponseEntity.notFound().build();
		
		
		
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true)
	public ResponseEntity<?> deletar(@PathVariable Long id){
		
		Optional<Topico> optional = topicoRepository.findById(id);
		if(optional.isPresent()) {
			topicoRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}
		
		return ResponseEntity.notFound().build();
		
		
	}
	
}
