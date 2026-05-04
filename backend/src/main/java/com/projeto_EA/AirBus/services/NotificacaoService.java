package com.projeto_EA.AirBus.services;

import com.projeto_EA.AirBus.models.DTOs.NotificacaoDTO;
import com.projeto_EA.AirBus.repositories.TituloTransporteRepository;
import com.projeto_EA.AirBus.repositories.ViagemRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final TituloTransporteRepository tituloRepository;
    private final ViagemRepository viagemRepository;

    @Transactional(readOnly = true)
    public List<NotificacaoDTO> getNotificacoes(Long userID) {
        List<NotificacaoDTO> notificacoes = new ArrayList<>();

        tituloRepository
            .findByUtenteId(userID)
            .stream()
            .flatMap(t -> t.gerarNotificacoes().stream())
            .forEach(notificacoes::add);

        viagemRepository
            .findIrregularByUtenteIDWithDetails(userID)
            .stream()
            .map(v ->
                new NotificacaoDTO(
                    "VIAGEM_IRREGULAR",
                    "A viagem na carreira " +
                        v.getCarreira().getNum() +
                        " (" +
                        v.getOrigem().getNome() +
                        " → " +
                        v.getDestino().getNome() +
                        ") foi registada como irregular.",
                    null
                )
            )
            .forEach(notificacoes::add);

        return notificacoes;
    }
}
