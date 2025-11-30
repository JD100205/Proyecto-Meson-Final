package com.restaurant.MesondelDesierto.controller;

import com.restaurant.MesondelDesierto.dto.MpOrderRequest;
import com.restaurant.MesondelDesierto.service.MercadoPagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mp")
@CrossOrigin(origins = "*")
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;

    public MercadoPagoController(MercadoPagoService mercadoPagoService) {
        this.mercadoPagoService = mercadoPagoService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPreference(@RequestBody MpOrderRequest request) {
        return ResponseEntity.ok(mercadoPagoService.createPreference(request));
    }
}


