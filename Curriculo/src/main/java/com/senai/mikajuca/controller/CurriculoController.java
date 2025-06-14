package com.senai.mikajuca.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.senai.mikajuca.builder.CurriculoBuilder;
import com.senai.mikajuca.model.Curriculo;
import com.senai.mikajuca.model.User;
import com.senai.mikajuca.repository.CurriculoRepository;
import com.senai.mikajuca.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CurriculoController {

    @Autowired
    private CurriculoRepository repository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/criar-curriculo")
    public String showForm(Model model) {
        model.addAttribute("curriculo", new Curriculo());
        model.addAttribute("modoEdicao", false);
        return "form";
    }

    @GetMapping("/curriculo/{id}")
    public String exibirDetalhesCurriculo(@PathVariable Long id, Model model) {
        Curriculo curriculo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Currículo não encontrado"));
    
        // Obter o usuário logado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
    
            // Verificar se o currículo pertence ao usuário logado
            if (!curriculo.getUser().getUsername().equals(username)) {
                throw new RuntimeException("Você não tem permissão para visualizar este currículo.");
            }
        }
    
        model.addAttribute("curriculo", curriculo);
        return "formDetalhes"; // nome do template a ser criado
    }
    @PostMapping("/salvar")
    public String salvarCurriculo(@ModelAttribute Curriculo curriculoForm, Model model) {
        if (curriculoForm.getEmail() == null || curriculoForm.getEmail().isBlank() ||
            curriculoForm.getTelefone() == null || curriculoForm.getTelefone().isBlank()) {
            model.addAttribute("erro", "Email e Telefone são obrigatórios.");
            model.addAttribute("modoEdicao", false);
            model.addAttribute("curriculo", curriculoForm);
            return "form";
        }
    
        // Verifica se o currículo já existe (id != null), caso contrário cria um novo
        Curriculo curriculo;
        if (curriculoForm.getId() != null) {
            // Atualiza o currículo existente
            curriculo = repository.findById(curriculoForm.getId())
                .orElseThrow(() -> new RuntimeException("Currículo não encontrado"));
            // Atualiza os dados do currículo existente
            curriculo.setNome(curriculoForm.getNome());
            curriculo.setProfissao(curriculoForm.getProfissao());
            curriculo.setEmail(curriculoForm.getEmail());
            curriculo.setTelefone(curriculoForm.getTelefone());
            curriculo.setEndereco(curriculoForm.getEndereco());
            curriculo.setLinkedin(curriculoForm.getLinkedin());
            curriculo.setGithub(curriculoForm.getGithub());
            curriculo.setResumoProfissional(curriculoForm.getResumoProfissional());
            curriculo.setExperienciaProfissional(curriculoForm.getExperienciaProfissional());
            curriculo.setFormacaoAcademica(curriculoForm.getFormacaoAcademica());
            curriculo.setHabilidades(curriculoForm.getHabilidades());
            curriculo.setIdiomas(curriculoForm.getIdiomas());
        } else {
            // Cria um novo currículo
            curriculo = new CurriculoBuilder()
                .comNome(curriculoForm.getNome())
                .comProfissao(curriculoForm.getProfissao())
                .comEmail(curriculoForm.getEmail())
                .comTelefone(curriculoForm.getTelefone())
                .comEndereco(curriculoForm.getEndereco())
                .comLinkedin(curriculoForm.getLinkedin())
                .comGithub(curriculoForm.getGithub())
                .comResumoProfissional(curriculoForm.getResumoProfissional())
                .comExperienciaProfissional(curriculoForm.getExperienciaProfissional())
                .comFormacaoAcademica(curriculoForm.getFormacaoAcademica())
                .comHabilidades(curriculoForm.getHabilidades())
                .comIdiomas(curriculoForm.getIdiomas())
                .build();
        }
    
        // Obter o usuário logado e associar ao currículo
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            User usuario = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            curriculo.setUser(usuario);
        }
    
        // Salva o currículo (novo ou atualizado)
        repository.save(curriculo);
        return "redirect:/main";
    }


    @GetMapping("/curriculo/{id}/pdf")
public void gerarPdf(@PathVariable Long id, HttpServletResponse response) throws IOException {
    Curriculo curriculo = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Currículo não encontrado"));
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=curriculo.pdf");

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font tituloFont = new Font(Font.HELVETICA, 22, Font.BOLD);
        Font secaoTituloFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Font textoFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

        Paragraph titulo = new Paragraph("Currículo", tituloFont);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        Paragraph nome = new Paragraph(curriculo.getNome(), new Font(Font.HELVETICA, 18, Font.BOLD));
        nome.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(nome);

        Paragraph profissao = new Paragraph(curriculo.getProfissao(), textoFont);
        profissao.setAlignment(Paragraph.ALIGN_CENTER);
        profissao.setSpacingAfter(15);
        document.add(profissao);

        String telefoneFormatado = formatarTelefone(curriculo.getTelefone());

        Paragraph contato = new Paragraph("Telefone: " + telefoneFormatado + " | Email: " + curriculo.getEmail()
                + "\nEndereço: " + curriculo.getEndereco(), textoFont);
        contato.setAlignment(Paragraph.ALIGN_CENTER);
        contato.setSpacingAfter(10);
        document.add(contato);

        if (curriculo.getLinkedin() != null && !curriculo.getLinkedin().isBlank()) {
            Paragraph linkedin = new Paragraph("LinkedIn: " + getPerfilNome(curriculo.getLinkedin()), textoFont);
            linkedin.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(linkedin);
        }

        if (curriculo.getGithub() != null && !curriculo.getGithub().isBlank()) {
            Paragraph github = new Paragraph("GitHub: " + getPerfilNome(curriculo.getGithub()), textoFont);
            github.setAlignment(Paragraph.ALIGN_CENTER);
            github.setSpacingAfter(20);
            document.add(github);
        }

        adicionarSecao(document, "Resumo Profissional", curriculo.getResumoProfissional(), secaoTituloFont, textoFont);
        adicionarSecao(document, "Experiência Profissional", curriculo.getExperienciaProfissional(), secaoTituloFont, textoFont);
        adicionarSecao(document, "Formação Acadêmica", curriculo.getFormacaoAcademica(), secaoTituloFont, textoFont);
        adicionarSecao(document, "Habilidades", curriculo.getHabilidades(), secaoTituloFont, textoFont);
        adicionarSecao(document, "Idiomas", curriculo.getIdiomas(), secaoTituloFont, textoFont);

        document.close();
    }

    private void adicionarSecao(Document document, String titulo, String conteudo, Font tituloFont, Font textoFont) throws DocumentException {
        if (conteudo == null || conteudo.isBlank()) return;

        Paragraph secaoTitulo = new Paragraph(titulo, tituloFont);
        secaoTitulo.setSpacingBefore(10);
        secaoTitulo.setSpacingAfter(5);
        document.add(secaoTitulo);

        Paragraph secaoConteudo = new Paragraph(conteudo, textoFont);
        secaoConteudo.setSpacingAfter(10);
        document.add(secaoConteudo);
    }

    private String getPerfilNome(String url) {
        if (url != null && url.contains("/")) {
            String[] partes = url.split("/");
            return partes[partes.length - 1];
        }
        return url;
    }

    @GetMapping("/curriculo/{id}/editar")
    public String editarCurriculo(@PathVariable Long id, Model model) {
        // Recupera o currículo do banco de dados
        Curriculo curriculo = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Currículo não encontrado"));
    
        // Obter o usuário logado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            
            // Verificar se o currículo pertence ao usuário logado
            if (!curriculo.getUser().getUsername().equals(username)) {
                throw new RuntimeException("Você não tem permissão para editar este currículo.");
            }
        }
    
        // Se a verificação passar, adiciona o currículo ao modelo para edição
        model.addAttribute("curriculo", curriculo);
        model.addAttribute("modoEdicao", true);
        return "form";
    }
    private String formatarTelefone(String telefone) {
        if (telefone == null) return "Não informado";
        telefone = telefone.replaceAll("[^0-9]", "");

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
            return telefone;
        }
    }

    @GetMapping("/curriculo/{id}/clonar")
    public String clonarCurriculo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Curriculo curriculoOriginal = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Currículo não encontrado"));
    
        // Obter o usuário logado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Verificar se o currículo pertence ao usuário logado
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
    
            // Verificar se o currículo pertence ao usuário logado
            if (!curriculoOriginal.getUser().getUsername().equals(username)) {
                throw new RuntimeException("Você não tem permissão para clonar este currículo.");
            }
        }
    
        try {
            // Clonando o currículo
            Curriculo curriculoClonado = curriculoOriginal.clone();
    
            // Limpando o ID do currículo clonado para garantir que seja tratado como um novo registro
            curriculoClonado.setId(null);  
    
            // Associando o currículo ao usuário logado
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
                String username = userDetails.getUsername();
                User usuario = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                curriculoClonado.setUser(usuario);
            }
    
            // Salvar o currículo clonado no banco
            repository.save(curriculoClonado);
    
            // Redirecionar para a página de edição do currículo clonado
            redirectAttributes.addFlashAttribute("curriculo", curriculoClonado);
            return "redirect:/curriculo/" + curriculoClonado.getId() + "/editar";
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Erro ao clonar o currículo", e);
        }
    }

    @PostMapping("/curriculo/{id}/excluir")
public String excluirCurriculo(@PathVariable Long id) {
    Curriculo curriculo = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Currículo não encontrado"));

    // Obter o usuário logado
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
        String username = userDetails.getUsername();

        // Verificar se o currículo pertence ao usuário logado
        if (!curriculo.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Você não tem permissão para excluir este currículo.");
        }
    }

    repository.delete(curriculo);
    return "redirect:/main";
}
}