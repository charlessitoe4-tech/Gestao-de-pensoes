package controller;

import java.util.List;
import java.util.Objects;

import model.Documento;
import model.enums.TipoDocumento;
import service.DocumentoService;

/** Controlador dos documentos: recebe os dados da interface e delega ao serviço. */
public class DocumentoController {

    private final DocumentoService service;

    public DocumentoController() {
        this(new DocumentoService());
    }

    public DocumentoController(DocumentoService service) {
        this.service = Objects.requireNonNull(service, "O serviço é obrigatório.");
    }

    public Documento guardar(TipoDocumento tipo, String numero, Long pessoaId, String caminhoFicheiro) {
        Documento documento = new Documento();
        documento.setTipo(tipo);
        documento.setNumero(numero == null || numero.isBlank() ? null : numero.trim());
        documento.setPessoaId(pessoaId);
        documento.setCaminhoFicheiro(caminhoFicheiro);
        documento.setDataEmissao(java.time.LocalDate.now());
        return service.guardar(documento);
    }

    public List<Documento> listar() {
        return service.listar();
    }

    public List<Documento> listarPorPessoa(Long pessoaId) {
        return service.listarPorPessoa(pessoaId);
    }

    public boolean remover(Long id) {
        return service.remover(id);
    }
}
