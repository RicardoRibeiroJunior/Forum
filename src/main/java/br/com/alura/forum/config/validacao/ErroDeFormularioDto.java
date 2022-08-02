package br.com.alura.forum.config.validacao;

public class ErroDeFormularioDto {

	private String erro;
	private String mensagem;

	public ErroDeFormularioDto(String erro, String mensagem) {
		super();
		this.erro = erro;
		this.mensagem = mensagem;
	}

	public String getErro() {
		return erro;
	}

	public String getMensagem() {
		return mensagem;
	}

}
