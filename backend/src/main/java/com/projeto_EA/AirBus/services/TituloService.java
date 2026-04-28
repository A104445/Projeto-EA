package com.projeto_EA.AirBus.services;

import com.projeto_EA.AirBus.models.DTOs.*;
import com.projeto_EA.AirBus.models.entities.*;
import com.projeto_EA.AirBus.models.enums.EstadoGeral;
import com.projeto_EA.AirBus.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TituloService {

    private final TituloTransporteRepository tituloRepository;
    private final UserRepository userRepository;
    private final CarreiraRepository carreiraRepository;
    private final ZonaGeograficaRepository zonaRepository;

    @Transactional(readOnly = true)
    public List<TituloDTO> getMeusTitulos(Long userID) {
        return tituloRepository.findByUtenteId(userID).stream()
                .map(TituloDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TituloDTO> getMeusTitulosAtivos(Long userID) {
        return tituloRepository.findByUtenteIdAndEstado(userID, EstadoGeral.ATIVO).stream()
                .map(TituloDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public TituloDTO adquirirPasseAnual(Long userID, AdquirirPasseAnualDTO dto) {
        UserEntity user = getUtente(userID);
        Set<ZonaGeograficaEntity> zonas = getZonas(dto.getZonaIDs());

        PasseAnualEntity passe = new PasseAnualEntity();
        passe.setUtente(user);
        passe.setDataInicio(LocalDate.now());
        passe.setDataFim(LocalDate.now().plusYears(1));
        passe.definirZonas(zonas);

        return TituloDTO.from(tituloRepository.save(passe));
    }

    @Transactional
    public TituloDTO adquirirBilheteIndividual(Long userID, AdquirirBilheteDTO dto) {
        UserEntity user = getUtente(userID);
        CarreiraEntity carreira = carreiraRepository.findById(dto.getCarreiraID())
                .orElseThrow(() -> new IllegalArgumentException("Carreira não encontrada!"));

        // Bilhete cobre as zonas das paragens da carreira.
        Set<ZonaGeograficaEntity> zonas = carreira.getParagens().stream()
                .flatMap(p -> p.getZonas().stream())
                .collect(Collectors.toSet());

        BilheteIndividualEntity bilhete = new BilheteIndividualEntity();
        bilhete.setUtente(user);
        bilhete.setCarreira(carreira);
        bilhete.setDataFim(LocalDate.now().plusDays(1));
        bilhete.definirZonas(zonas);

        return TituloDTO.from(tituloRepository.save(bilhete));
    }

    @Transactional
    public TituloDTO adquirirPacote(Long userID, AdquirirPacoteDTO dto) {
        UserEntity user = getUtente(userID);
        Set<ZonaGeograficaEntity> zonas = getZonas(dto.getZonaIDs());

        PacoteViagensEntity pacote = new PacoteViagensEntity();
        pacote.setUtente(user);
        pacote.setNumViagensTotais(dto.getNumViagens());
        pacote.setSaldoRestante(dto.getNumViagens());
        pacote.definirZonas(zonas);

        return TituloDTO.from(tituloRepository.save(pacote));
    }

    private UserEntity getUtente(Long userID) {
        return userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado!"));
    }

    private Set<ZonaGeograficaEntity> getZonas(Set<Long> zonaIds) {
        return zonaIds.stream()
                .map(id -> zonaRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Zona não encontrada: " + id)))
                .collect(Collectors.toSet());
    }
}