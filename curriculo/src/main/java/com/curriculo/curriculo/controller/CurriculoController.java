package com.curriculo.curriculo.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.curriculo.curriculo.model.Curriculo;
import com.curriculo.curriculo.repository.CurriculoRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CurriculoController {

    @Autowired
    private CurriculoRepository repository;

    @GetMapping("/")
    public String showForm(Model model) {
    model.addAttribute("curriculo", new Curriculo());
    model.addAttribute("modoEdicao", false); // <<< adiciona a flag no novo também
    return "form";
}

    @PostMapping("/salvar")
    public String salvarCurriculo(@ModelAttribute Curriculo curriculo) {
        repository.save(curriculo);
        return "redirect:/curriculo";
    }

    @GetMapping("/curriculo")
    public String exibirCurriculo(Model model) {
        Curriculo curriculo = repository.findAll()
                .stream()
                .reduce((first, second) -> second)
                .orElse(new Curriculo()); // pega o último inserido
        model.addAttribute("curriculo", curriculo);
        return "curriculo";
    }

    @GetMapping("/curriculo/pdf")
    public void gerarPdf(HttpServletResponse response) throws IOException {
        // Buscar o último currículo inserido, em vez de buscar por ID fixo
        Curriculo curriculo = repository.findAll()
                .stream()
                .reduce((first, second) -> second)  // Pega o último inserido
                .orElse(new Curriculo()); // Caso não tenha nenhum, cria um currículo em branco

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=curriculo.pdf");

        Document document = new Document(PageSize.A4, 50, 50, 50, 50); // margem
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // Títulos e fontes
        Font tituloFont = new Font(Font.HELVETICA, 22, Font.BOLD);
        Font secaoTituloFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font textoFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

        // Título principal
        Paragraph titulo = new Paragraph("Currículo", tituloFont);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // Nome e Profissão
        Paragraph nome = new Paragraph(curriculo.getNome(), new Font(Font.HELVETICA, 18, Font.BOLD));
        nome.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(nome);

        Paragraph profissao = new Paragraph(curriculo.getProfissao(), textoFont);
        profissao.setAlignment(Paragraph.ALIGN_CENTER);
        profissao.setSpacingAfter(15);
        document.add(profissao);

        // Contato - Extrair nome do LinkedIn e GitHub
        String linkedinNome = curriculo.getLinkedin() != null ? getPerfilNome(curriculo.getLinkedin()) : "Não informado";
        String githubNome = curriculo.getGithub() != null ? getPerfilNome(curriculo.getGithub()) : "Não informado";

        Paragraph contato = new Paragraph(
                "Telefone: " + curriculo.getTelefone() + " | Email: " + curriculo.getEmail() + "\n" +
                "Endereço: " + curriculo.getEndereco() + "\n" +
                "LinkedIn: " + linkedinNome + " | GitHub: " + githubNome,
                textoFont
        );
        contato.setAlignment(Paragraph.ALIGN_CENTER);
        contato.setSpacingAfter(20);
        document.add(contato);

        // Função para criar seção
        adicionarSecao(document, "Resumo Profissional", curriculo.getResumoProfissional(), secaoTituloFont, textoFont);
        adicionarSecao(document, "Experiência Profissional", curriculo.getExperienciaProfissional(), secaoTituloFont, textoFont);
        adicionarSecao(document, "Formação Acadêmica", curriculo.getFormacaoAcademica(), secaoTituloFont, textoFont);
        adicionarSecao(document, "Habilidades", curriculo.getHabilidades(), secaoTituloFont, textoFont);
        adicionarSecao(document, "Idiomas", curriculo.getIdiomas(), secaoTituloFont, textoFont);

        document.close();
    }

    // Função para extrair nome de perfil do LinkedIn ou GitHub
    private String getPerfilNome(String url) {
        if (url != null) {
            // Tenta extrair o nome do perfil, levando em consideração a URL do LinkedIn ou GitHub
            String[] partes = url.split("/");
            if (partes.length > 0) {
                return partes[partes.length - 1];  // Pega a última parte da URL
            }
        }
        return "Não informado";
    }

    private void adicionarSecao(Document document, String titulo, String conteudo, Font tituloFont, Font textoFont) throws DocumentException {
        if (conteudo == null || conteudo.isEmpty()) {
            conteudo = "Não informado.";
        }

        // Adiciona título da seção
        Paragraph secaoTitulo = new Paragraph(titulo, tituloFont);
        secaoTitulo.setSpacingBefore(10);
        secaoTitulo.setSpacingAfter(5);
        document.add(secaoTitulo);

        // Adiciona conteúdo da seção
        Paragraph secaoConteudo = new Paragraph(conteudo, textoFont);
        secaoConteudo.setSpacingAfter(10);
        document.add(secaoConteudo);
    }
    @GetMapping("/editar")
    public String editarCurriculo(Model model) {
        Curriculo curriculo = repository.findAll()
                .stream()
                .reduce((first, second) -> second)
                .orElse(new Curriculo());
        model.addAttribute("curriculo", curriculo);
        model.addAttribute("modoEdicao", true); // <<< adiciona a flag
        return "form";
    }
}
