package com.projeto_EA.AirBus.services;

import com.projeto_EA.AirBus.models.DTOs.NotificacaoDTO;
import com.projeto_EA.AirBus.models.entities.PacoteViagensEntity;
import com.projeto_EA.AirBus.models.entities.PasseAnualEntity;
import com.projeto_EA.AirBus.models.entities.TituloTransporteEntity;
import com.projeto_EA.AirBus.models.entities.ViagemEntity;
import com.projeto_EA.AirBus.repositories.TituloTransporteRepository;
import com.projeto_EA.AirBus.repositories.ViagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private static final int DIAS_AVISO_PASSE = 30;
    private static final int SALDO_MINIMO_PACK = 2;

    private final TituloTransporteRepository tituloRepository;
    private final ViagemRepository viagemRepository;

    public List<NotificacaoDTO> getNotificacoes(Long userID) {
        List<TituloTransporteEntity> titulos = tituloRepository.findByUtenteID(userID);
        List<NotificacaoDTO> notificacoes = new ArrayList<>();

        for (TituloTransporteEntity titulo : titulos) {
            if (titulo instanceof PasseAnualEntity passe) {
                if (passe.getDataFim() == null) continue;
                long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), passe.getDataFim());
                if (diasRestantes < 0) {
                    notificacoes.add(new NotificacaoDTO(
                        "PASSE_EXPIRADO",
                        "O teu passe anual expirou.",
                        titulo.getID()
                    ));
                } else if (diasRestantes <= DIAS_AVISO_PASSE) {
                    notificacoes.add(new NotificacaoDTO(
                        "PASSE_EXPIRANDO",
                        "O teu passe anual expira em " + diasRestantes + " dia(s).",
                        titulo.getID()
                    ));
                }
            } else if (titulo instanceof PacoteViagensEntity pack) {
                if (pack.getSaldoRestante() <= SALDO_MINIMO_PACK) {
                    notificacoes.add(new NotificacaoDTO(
                        "SALDO_BAIXO",
                        "Já só tens apenas " + pack.getSaldoRestante() + " viagem(s) restante(s) no teu pacote de viagens.",
                        titulo.getID()
                    ));
                }
            }
        }

        List<ViagemEntity> viagens = viagemRepository.findByUtenteIDAndIrregularTrue(userID);
        for (ViagemEntity viagem : viagens) {
            notificacoes.add(new NotificacaoDTO(
                "VIAGEM_IRREGULAR",
                "A viagem na carreira " + viagem.getCarreira().getNum() +
                " (" + viagem.getOrigem().getNome() + " → " + viagem.getDestino().getNome() +
                ") foi registada como irregular.",
                null
            ));
        }

        return notificacoes;
    }
}
