package com.senai.mikajuca.builder;

import com.senai.mikajuca.model.Curriculo;

public class CurriculoBuilder {
    private Curriculo curriculo;

    public CurriculoBuilder() {
        curriculo = new Curriculo();
    }

    public CurriculoBuilder comNome(String nome) {
        curriculo.setNome(nome);
        return this;
    }

    public CurriculoBuilder comProfissao(String profissao) {
        curriculo.setProfissao(profissao);
        return this;
    }

    public CurriculoBuilder comEmail(String email) {
        curriculo.setEmail(email);
        return this;
    }

    public CurriculoBuilder comTelefone(String telefone) {
        curriculo.setTelefone(telefone);
        return this;
    }

    public CurriculoBuilder comEndereco(String endereco) {
        curriculo.setEndereco(endereco);
        return this;
    }

    public CurriculoBuilder comLinkedin(String linkedin) {
        curriculo.setLinkedin(linkedin);
        return this;
    }

    public CurriculoBuilder comGithub(String github) {
        curriculo.setGithub(github);
        return this;
    }

    public CurriculoBuilder comResumoProfissional(String resumo) {
        curriculo.setResumoProfissional(resumo);
        return this;
    }

    public CurriculoBuilder comExperienciaProfissional(String experiencia) {
        curriculo.setExperienciaProfissional(experiencia);
        return this;
    }

    public CurriculoBuilder comFormacaoAcademica(String formacao) {
        curriculo.setFormacaoAcademica(formacao);
        return this;
    }

    public CurriculoBuilder comHabilidades(String habilidades) {
        curriculo.setHabilidades(habilidades);
        return this;
    }

    public CurriculoBuilder comIdiomas(String idiomas) {
        curriculo.setIdiomas(idiomas);
        return this;
    }

    public Curriculo build() {
        return curriculo;
    }
}