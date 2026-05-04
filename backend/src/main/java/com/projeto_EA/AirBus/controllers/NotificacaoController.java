package com.projeto_EA.AirBus.controllers;

import com.projeto_EA.AirBus.models.DTOs.NotificacaoDTO;
import com.projeto_EA.AirBus.routes.Routes;
import com.projeto_EA.AirBus.security.UserDetailsAdapter;
import com.projeto_EA.AirBus.services.NotificacaoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Routes.NOTIFICACOES)
@RequiredArgsConstructor
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<List<NotificacaoDTO>> getNotificacoes(
        @AuthenticationPrincipal UserDetailsAdapter principal
    ) {
        return ResponseEntity.ok(
            notificacaoService.getNotificacoes(principal.getUser().getId())
        );
    }
}
