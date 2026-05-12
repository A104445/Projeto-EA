package com.projeto_EA.AirBus.services;

import com.projeto_EA.AirBus.models.DTOs.*;
import com.projeto_EA.AirBus.models.entities.*;
import com.projeto_EA.AirBus.models.enums.EstadoViagem;
import com.projeto_EA.AirBus.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViagemService {

    private final ViagemRepository viagemRepository;
    private final UserRepository userRepository;
    private final TituloTransporteRepository tituloRepository;
    private final CarreiraRepository carreiraRepository;
    private final ParagemRepository paragemRepository;
    private final VeiculoRepository veiculoRepository;
    private final HorarioRepository horarioRepository;

    @Transactional
    public ViagemDTO checkIn(Long userID, CheckInRequestDTO dto) {
        if (viagemRepository.findByUtenteIdAndEstado(userID, EstadoViagem.EM_CURSO).isPresent()) {
            throw new IllegalStateException("Já existe uma viagem em curso! Faça check-out primeiro...");
        }

        UserEntity user = getUtente(userID);
        ParagemEntity paragem = getParagem(dto.getParagemID());
        CarreiraEntity carreira = getCarreira(dto.getCarreiraID());

        if (!carreira.getParagens().contains(paragem)) {
            throw new IllegalArgumentException("A carreira selecionada não passa nesta paragem!");
        }

        TituloTransporteEntity titulo = tituloRepository.findById(dto.getTituloID())
                .orElseThrow(() -> new IllegalArgumentException("Título não encontrado!"));

        if (!titulo.getUtente().getId().equals(userID)) {
            throw new IllegalArgumentException("Título não pertence ao utilizador!");
        }

        if (!titulo.isValid()) {
            throw new IllegalStateException("Título inválido ou expirado!");
        }

        titulo.validarParaCheckIn(carreira,paragem);

        ViagemEntity viagem = new ViagemEntity();
        viagem.setUtente(user);
        viagem.setTitulo(titulo);
        viagem.setCarreira(carreira);
        viagem.setOrigem(paragem);
        viagem.setDataHoraEntrada(LocalDateTime.now());

        VeiculoEntity veiculo = dto.getVeiculoID() != null
                ? veiculoRepository.findById(dto.getVeiculoID()).orElse(null)
                : findNextVeiculo(carreira, paragem);
        viagem.setVeiculo(veiculo);
        if (veiculo != null) {
            veiculoRepository.incrementarOcupacao(veiculo.getId());
        }

        return new ViagemDTO(viagemRepository.save(viagem));
    }

    @Transactional
    public ViagemDTO checkOut(Long userID, Long viagemID, CheckOutRequestDTO dto) {
        ViagemEntity viagem = viagemRepository.findById(viagemID)
                .orElseThrow(() -> new IllegalArgumentException("Viagem não encontrada!"));

        if (!viagem.getUtente().getId().equals(userID)) {
            throw new IllegalArgumentException("Viagem não pertence ao utilizador!");
        }

        if (EstadoViagem.EM_CURSO != viagem.getEstado()) {
            throw new IllegalStateException("Esta viagem já foi concluída!");
        }

        ParagemEntity destino = getParagem(dto.getParagemID());

        Set<ZonaGeograficaEntity> zonasPercorridas = calcularZonasPercorridas(
                viagem.getCarreira(), viagem.getOrigem(), destino);

        TituloTransporteEntity titulo = viagem.getTitulo();
        viagem.setDestino(destino);
        viagem.setDataHoraSaida(LocalDateTime.now());
        viagem.setEstado(EstadoViagem.CONCLUIDA);
        viagem.setIrregular(titulo.isIrregular(zonasPercorridas));

        titulo.aoFinalizarViagem();
        tituloRepository.save(titulo);

        if (viagem.getVeiculo() != null) {
            veiculoRepository.decrementarOcupacao(viagem.getVeiculo().getId());
        }

        return new ViagemDTO(viagemRepository.save(viagem));
    }

    @Transactional(readOnly = true)
    public Optional<ViagemDTO> getViagemAtiva(Long userID) {
        return viagemRepository.findByUtenteIdAndEstado(userID, EstadoViagem.EM_CURSO)
                .map(ViagemDTO::new);
    }

    @Transactional(readOnly = true)
    public List<ViagemDTO> getHistorico(Long userID) {
        return viagemRepository.findByUtenteIdOrderByDataHoraEntradaDesc(userID).stream()
                .map(ViagemDTO::new)
                .collect(Collectors.toList());
    }

    private VeiculoEntity findNextVeiculo(CarreiraEntity carreira, ParagemEntity paragem) {
        List<VeiculoEntity> veiculos = veiculoRepository.findByCarreiraIdOrderById(carreira.getId());
        if (veiculos.isEmpty()) return null;

        int diaSemana = LocalDate.now().getDayOfWeek().getValue();
        LocalTime agora = LocalTime.now();

        List<HorarioEntity> horarios = horarioRepository
                .findByCarreiraIdAndParagemIdAndDiaSemanaOrderByHoraChegada(
                        carreira.getId(), paragem.getId(), diaSemana);

        int idx = horarios.size() - 1; // fallback: último horário do dia
        for (int i = 0; i < horarios.size(); i++) {
            if (!horarios.get(i).getHoraChegada().isBefore(agora)) {
                idx = i;
                break;
            }
        }

        return veiculos.get(idx % veiculos.size());
    }

    private Set<ZonaGeograficaEntity> calcularZonasPercorridas(
            CarreiraEntity carreira, ParagemEntity origem, ParagemEntity destino) {

        List<ParagemEntity> paragens = carreira.getParagens();
        int idxOrigem  = paragens.indexOf(origem);
        int idxDestino = paragens.indexOf(destino);

        if (idxOrigem == -1 || idxDestino == -1) {
            Set<ZonaGeograficaEntity> zonas = new java.util.HashSet<>(origem.getZonas());
            zonas.addAll(destino.getZonas());
            return zonas;
        }

        int start = Math.min(idxOrigem, idxDestino);
        int end   = Math.max(idxOrigem, idxDestino);

        return paragens.subList(start, end + 1).stream()
                .flatMap(p -> p.getZonas().stream())
                .collect(Collectors.toSet());
    }

    private UserEntity getUtente(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado!"));
    }

    private ParagemEntity getParagem(Long id) {
        return paragemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paragem não encontrada!"));
    }

    private CarreiraEntity getCarreira(Long id) {
        return carreiraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carreira não encontrada!"));
    }
}