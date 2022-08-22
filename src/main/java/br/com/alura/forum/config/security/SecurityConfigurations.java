package br.com.alura.forum.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfigurations extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private AutenticacaoService autenticacaoService;
	
	@Override
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}
	
	//Usado para configurações de autenticação
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(autenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
	}
	
	
	//Usado para configurações de autorização das URL´s
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests()
			.antMatchers(HttpMethod.GET, "/topicos").permitAll()//liberando URL publica para listar.
			.antMatchers(HttpMethod.GET, "/topicos/*").permitAll()//liberando URL para detalhar um tópico (*).
			.antMatchers(HttpMethod.POST, "/auth").permitAll()
			.anyRequest().authenticated()//Qualquer outra requisição tem que está autenticado.
			.and().csrf().disable()//desabilita a segurança csrf
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//informa que será usado token na comunicação
	}
	
	
	
	//Usado para configurações de recursos estáticos (Acesso as js, css, img)
	@Override
	public void configure(WebSecurity web) throws Exception {
	}

}
