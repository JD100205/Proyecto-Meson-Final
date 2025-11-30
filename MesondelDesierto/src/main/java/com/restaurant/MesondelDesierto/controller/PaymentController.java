package com.restaurant.MesondelDesierto.controller;

import com.restaurant.MesondelDesierto.dto.MpOrderRequest;
import com.restaurant.MesondelDesierto.service.MercadoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private MercadoPagoService mpService;

    @PostMapping("/create_preference")
    public ResponseEntity<?> createPreference(@RequestBody MpOrderRequest orderRequest) {
        try {
            Map<String, Object> mpResponse = mpService.createPreference(orderRequest);
            // mpResponse contiene: id, init_point, sandbox_init_point, etc.
            return ResponseEntity.ok(mpResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // Webhook (notification) — Mercado Pago enviará un POST (topic, id) o GET según configuración
    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestParam(required = false) String topic, @RequestParam(required = false) String id, @RequestBody(required = false) Map<String,Object> body) {
        // Respuesta rápida para Mercado Pago (200)
        // Aquí debes validar: si topic=payment -> obtener payment info llamando a getPayment(id)
        // Guarda estado en tu BD según external_reference
        System.out.println("MP webhook - topic: " + topic + " id: " + id + " body: " + body);
        return ResponseEntity.ok(Map.of("status", "received"));
    }
}
