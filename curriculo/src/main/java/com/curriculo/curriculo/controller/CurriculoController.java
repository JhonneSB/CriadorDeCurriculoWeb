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
        Curriculo curriculo = repository.findAll()
                .stream()
                .reduce((first, second) -> second)
                .orElse(new Curriculo());

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=curriculo.pdf");

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
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

        // Formatações auxiliares
        String telefoneFormatado = formatarTelefone(curriculo.getTelefone());
        String linkedinNome = curriculo.getLinkedin() != null ? getPerfilNome(curriculo.getLinkedin()) : "Não informado";
        String githubNome = curriculo.getGithub() != null ? getPerfilNome(curriculo.getGithub()) : "Não informado";

        // Contato
        Paragraph contato = new Paragraph(
                "Telefone: " + telefoneFormatado + " | Email: " + curriculo.getEmail() + "\n" +
                "Endereço: " + curriculo.getEndereco() + "\n" +
                "LinkedIn: " + linkedinNome + " | GitHub: " + githubNome,
                textoFont
        );
        contato.setAlignment(Paragraph.ALIGN_CENTER);
        contato.setSpacingAfter(20);
        document.add(contato);

        // Seções
        adicionarSecao(document, "Resumo Profissional", curriculo.getResumoProfissional(), secaoTituloFont, textoFont);
        adicionarSecao(document, "Experiência Profissional", curriculo.getExperienciaProfissional(), secaoTituloFont, textoFont);
        adicionarSecao(document, "Formação Acadêmica", curriculo.getFormacaoAcademica(), secaoTituloFont, textoFont);
        adicionarSecao(document, "Habilidades", curriculo.getHabilidades(), secaoTituloFont, textoFont);
        adicionarSecao(document, "Idiomas", curriculo.getIdiomas(), secaoTituloFont, textoFont);

        document.close();
    }

    private String getPerfilNome(String url) {
        if (url != null) {
            String[] partes = url.split("/");
            if (partes.length > 0) {
                return partes[partes.length - 1];
            }
        }
        return "Não informado";
    }

    private void adicionarSecao(Document document, String titulo, String conteudo, Font tituloFont, Font textoFont) throws DocumentException {
        if (conteudo == null || conteudo.isEmpty()) {
            conteudo = "Não informado.";
        }

        Paragraph secaoTitulo = new Paragraph(titulo, tituloFont);
        secaoTitulo.setSpacingBefore(10);
        secaoTitulo.setSpacingAfter(5);
        document.add(secaoTitulo);

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
        model.addAttribute("modoEdicao", true);
        return "form";
    }

    // Função para formatar telefone no estilo (11) 91234-5678
    private String formatarTelefone(String telefone) {
        if (telefone == null) return "Não informado";
        telefone = telefone.replaceAll("[^0-9]", ""); // remove tudo que não é número

        if (telefone.length() == 11) {
            return String.format("(%s) %s-%s",
                    telefone.substring(0, 2),
                    telefone.substring(2, 7),
                    telefone.substring(7));
        } else if (telefone.length() == 10) {
            return String.format("(%s) %s-%s",
                    telefone.substring(0, 2),
                    telefone.substring(2, 6),
                    telefone.substring(6));
        } else {
            return telefone; // se não tiver tamanho certo, retorna cru
        }
    }
}
