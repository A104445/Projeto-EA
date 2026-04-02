package com.projeto_EA.AirBus.services;

import com.projeto_EA.AirBus.models.DTOs.CarreiraDTO;
import com.projeto_EA.AirBus.models.DTOs.ParagemDTO;
import com.projeto_EA.AirBus.models.entities.ParagemEntity;
import com.projeto_EA.AirBus.repositories.CarreiraRepository;
import com.projeto_EA.AirBus.repositories.ParagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParagemService {

    private final ParagemRepository paragemRepository;
    private final CarreiraRepository carreiraRepository;

    private static final double EARTH_RADIUS_KM = 6371.0;

    @Transactional(readOnly = true)
    public List<ParagemDTO> getAllParagens() {
        return paragemRepository.findByEstado("ATIVO").stream()
                .map(ParagemDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParagemDTO> getProximas(double lat, double lng, int limite) {
        List<ParagemEntity> paragens = paragemRepository.findNearestByCoordinates(lat, lng);
        return paragens.stream()
                .limit(limite)
                .map(p -> {
                    ParagemDTO dto = new ParagemDTO(p);
                    dto.setDistanciaKm(haversine(lat, lng, p.getLatitude(), p.getLongitude()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CarreiraDTO> getCarreirasByParagem(Long paragemID) {
        return carreiraRepository.findByParagemID(paragemID).stream()
                .map(CarreiraDTO::new)
                .collect(Collectors.toList());
    }

    private double haversine(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}