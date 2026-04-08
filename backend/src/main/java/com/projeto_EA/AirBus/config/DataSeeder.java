package com.projeto_EA.AirBus.config;

import com.projeto_EA.AirBus.models.entities.*;
import com.projeto_EA.AirBus.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final ZonaGeograficaRepository zonaRepo;
    private final ParagemRepository paragemRepo;
    private final VeiculoRepository veiculoRepo;
    private final CarreiraRepository carreiraRepo;
    private final HorarioRepository horarioRepo;

    @Override
    @Transactional
    public void run(String... args) {
        if (zonaRepo.count() > 0) {
            log.info("Base de dados já inicializada, a saltar seeder...");
            return;
        }

        log.info("A inicializar dados static...");

        // === ZONAS GEOGRÁFICAS ===
        ZonaGeograficaEntity zonaA = zonaRepo.save(new ZonaGeograficaEntity("Zona A - Centro"));
        ZonaGeograficaEntity zonaB = zonaRepo.save(new ZonaGeograficaEntity("Zona B - Suburbana"));
        ZonaGeograficaEntity zonaC = zonaRepo.save(new ZonaGeograficaEntity("Zona C - Periférica"));

        // === PARAGENS ===
        // Carreira 1: Estação → UMinho (Zona A → B)
        ParagemEntity estacaoBraga   = criarParagem("Estação de Braga",       "QR-ESTACAO-BRAGA",    List.of(zonaA));
        ParagemEntity avCentral      = criarParagem("Avenida Central",         "QR-AV-CENTRAL",       List.of(zonaA));
        ParagemEntity pracaRepublica = criarParagem("Praça da República",      "QR-PRACA-REPUBLICA",  List.of(zonaA));
        ParagemEntity laranjais      = criarParagem("Laranjais",               "QR-LARANJAIS",        List.of(zonaA, zonaB));
        ParagemEntity uminho         = criarParagem("Universidade do Minho",   "QR-UMINHO",           List.of(zonaB));

        // Carreira 2: Centro → Guimarães (Zona A → C)
        ParagemEntity terminal       = criarParagem("Terminal de Braga",       "QR-TERMINAL-BRAGA",   List.of(zonaA));
        ParagemEntity braga          = criarParagem("Braga (Rotunda)",         "QR-BRAGA-ROTUNDA",    List.of(zonaA, zonaB));
        ParagemEntity fafe           = criarParagem("Fafe",                    "QR-FAFE",             List.of(zonaC));
        ParagemEntity guimaraes      = criarParagem("Guimarães Centro",        "QR-GUIMARAES",        List.of(zonaC));

        // Carreira 3: Loop Centro Braga (Zona A/B)
        ParagemEntity bomJesus       = criarParagem("Bom Jesus",               "QR-BOM-JESUS",        List.of(zonaB));
        ParagemEntity seBraga        = criarParagem("Sé de Braga",             "QR-SE-BRAGA",         List.of(zonaA));

        // === VEÍCULOS ===
        VeiculoEntity v1 = veiculoRepo.save(new VeiculoEntity("BR-01-AA", 50));
        VeiculoEntity v2 = veiculoRepo.save(new VeiculoEntity("BR-02-BB", 45));
        VeiculoEntity v3 = veiculoRepo.save(new VeiculoEntity("BR-03-CC", 60));

        // === CARREIRAS ===
        CarreiraEntity c1 = new CarreiraEntity("1", "Estação → Universidade do Minho");
        c1.setParagens(List.of(estacaoBraga, avCentral, pracaRepublica, laranjais, uminho));
        c1.setVeiculo(v1);
        carreiraRepo.save(c1);

        CarreiraEntity c2 = new CarreiraEntity("2", "Braga → Guimarães");
        c2.setParagens(List.of(terminal, braga, fafe, guimaraes));
        c2.setVeiculo(v2);
        carreiraRepo.save(c2);

        CarreiraEntity c3 = new CarreiraEntity("3", "Loop Centro Braga");
        c3.setParagens(List.of(estacaoBraga, seBraga, avCentral, pracaRepublica, bomJesus, estacaoBraga));
        c3.setVeiculo(v3);
        carreiraRepo.save(c3);

        // === HORÁRIOS (dias úteis = 1-5) ===
        // Carreira 1 — de hora em hora das 7h às 22h
        for (int dia = 1; dia <= 5; dia++) {
            for (int hora = 7; hora <= 22; hora++) {
                horarioRepo.save(new HorarioEntity(c1, estacaoBraga,  LocalTime.of(hora, 0),  dia));
                horarioRepo.save(new HorarioEntity(c1, avCentral,     LocalTime.of(hora, 5),  dia));
                horarioRepo.save(new HorarioEntity(c1, pracaRepublica, LocalTime.of(hora, 10), dia));
                horarioRepo.save(new HorarioEntity(c1, laranjais,     LocalTime.of(hora, 20), dia));
                horarioRepo.save(new HorarioEntity(c1, uminho,        LocalTime.of(hora, 30), dia));
            }
        }

        // Carreira 2 — 3 vezes por dia
        int[] horasC2 = {8, 12, 17};
        for (int dia = 1; dia <= 5; dia++) {
            for (int hora : horasC2) {
                horarioRepo.save(new HorarioEntity(c2, terminal,   LocalTime.of(hora, 0),  dia));
                horarioRepo.save(new HorarioEntity(c2, braga,      LocalTime.of(hora, 15), dia));
                horarioRepo.save(new HorarioEntity(c2, fafe,       LocalTime.of(hora, 45), dia));
                horarioRepo.save(new HorarioEntity(c2, guimaraes,  LocalTime.of(hora, 55), dia));
            }
        }

        // Carreira 3 — de 30 em 30 min das 8h às 20h
        for (int dia = 1; dia <= 5; dia++) {
            for (int hora = 8; hora <= 20; hora++) {
                for (int min : new int[]{0, 30}) {
                    horarioRepo.save(new HorarioEntity(c3, estacaoBraga, LocalTime.of(hora, min),      dia));
                    horarioRepo.save(new HorarioEntity(c3, seBraga,      LocalTime.of(hora, min + 5),  dia));
                    horarioRepo.save(new HorarioEntity(c3, avCentral,    LocalTime.of(hora, min + 10), dia));
                }
            }
        }

        log.info("Seeder concluído: {} zonas, {} paragens, {} carreiras.",
                zonaRepo.count(), paragemRepo.count(), carreiraRepo.count());
    }

    private ParagemEntity criarParagem(String nome, String qr,
                                        List<ZonaGeograficaEntity> zonas) {
        ParagemEntity p = new ParagemEntity(nome, qr);
        p.getZonas().addAll(zonas);
        return paragemRepo.save(p);
    }
}