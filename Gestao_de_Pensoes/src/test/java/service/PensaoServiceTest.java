package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import dao.PensaoDAO;
import model.Pensao;
import model.PensaoVelhice;
import model.enums.EstadoPensao;
import org.junit.jupiter.api.Test;
import repository.RepositorioEmMemoria;

class PensaoServiceTest {

    private static final class RepositorioComIds
            extends RepositorioEmMemoria<Pensao, Long> implements PensaoDAO {

        private long proximoId;

        private RepositorioComIds() {
            super(Pensao::getId);
        }

        @Override
        public synchronized void criar(Pensao pensao) {
            if (pensao.getId() == null) {
                pensao.setId(++proximoId);
            }
            super.criar(pensao);
        }
    }

    @Test
    void gereCicloDeVidaEValorMensalDasPensoesAtivas() {
        PensaoService servico = new PensaoService(new RepositorioComIds());
        Pensao primeira = servico.guardar(novaPensao("PROC-1", "1250.50"));
        Pensao segunda = servico.guardar(novaPensao("PROC-2", "749.50"));

        assertEquals(EstadoPensao.PENDENTE, primeira.getEstado());
        assertEquals(EstadoPensao.PENDENTE, segunda.getEstado());
        assertEquals(0, servico.calcularTotalMensalAtivo().compareTo(BigDecimal.ZERO));

        servico.aprovar(primeira.getId());
        servico.aprovar(segunda.getId());
        assertEquals(0, servico.calcularTotalMensalAtivo()
                .compareTo(new BigDecimal("2000.00")));

        servico.suspender(primeira.getId());
        assertEquals(0, servico.calcularTotalMensalAtivo()
                .compareTo(new BigDecimal("749.50")));
        servico.reativar(primeira.getId());
        servico.cancelar(segunda.getId());

        assertEquals(EstadoPensao.CANCELADA,
                servico.buscarPorId(segunda.getId()).orElseThrow().getEstado());
        assertFalse(servico.buscarPorId(segunda.getId()).orElseThrow().getDataFim() == null);
        assertEquals(1, servico.pesquisar("", null, "ATIVA", null).size());
    }

    @Test
    void rejeitaTransicoesInvalidasEEliminacaoDePensaoAtiva() {
        PensaoService servico = new PensaoService(new RepositorioComIds());
        Pensao pensao = servico.guardar(novaPensao("PROC-1", "1000"));

        assertThrows(IllegalStateException.class, () -> servico.suspender(pensao.getId()));
        servico.aprovar(pensao.getId());
        assertThrows(IllegalStateException.class, () -> servico.remover(pensao.getId()));
        servico.cancelar(pensao.getId());
        servico.arquivar(pensao.getId());
        assertThrows(IllegalStateException.class, () -> servico.reativar(pensao.getId()));
        assertTrue(servico.remover(pensao.getId()));
    }

    @Test
    void rejeitaValoresMensaisNaoPositivos() {
        PensaoService servico = new PensaoService(new RepositorioComIds());
        Pensao pensao = novaPensao("PROC-1", "0");

        assertThrows(IllegalArgumentException.class, () -> servico.guardar(pensao));
    }

    private Pensao novaPensao(String processo, String valor) {
        Pensao pensao = new PensaoVelhice();
        pensao.setNumeroProcesso(processo);
        pensao.setPensionistaId(1L);
        pensao.setValorMensal(new BigDecimal(valor));
        pensao.setDataInicio(LocalDate.of(2026, 1, 1));
        pensao.setEstado(EstadoPensao.PENDENTE);
        return pensao;
    }
}
