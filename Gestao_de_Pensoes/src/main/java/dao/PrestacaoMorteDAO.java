package dao;

import java.util.List;
import java.util.Optional;
import model.PrestacaoMorte;
import model.enums.TipoPrestacao;

/** Contrato de acesso a dados para prestações por morte. */
public interface PrestacaoMorteDAO {

    void criar(PrestacaoMorte prestacao);

    Optional<PrestacaoMorte> buscarPorId(Long id);

    List<PrestacaoMorte> listarTodos();

    List<PrestacaoMorte> listarPorTipo(TipoPrestacao tipo);

    List<PrestacaoMorte> listarPorFalecido(Long falecidoId);

    List<PrestacaoMorte> listarPorRequerente(Long requerenteId);

    List<PrestacaoMorte> listarAprovadas();

    List<PrestacaoMorte> listarPendentes();

    void atualizar(Long id, PrestacaoMorte prestacao);

    boolean remover(Long id);
}