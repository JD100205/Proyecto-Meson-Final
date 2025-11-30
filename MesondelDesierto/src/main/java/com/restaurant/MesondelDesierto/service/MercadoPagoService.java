package com.restaurant.MesondelDesierto.service;

import com.restaurant.MesondelDesierto.dto.MpOrderRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class MercadoPagoService {

    private final RestTemplate restTemplate;

    @Value("${mp.access.token}")
    private String accessToken;

    public MercadoPagoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Crea una preference en Mercado Pago con la REST API oficial (sin SDK).
     */
    public Map<String, Object> createPreference(MpOrderRequest orderRequest) {

        String url = "https://api.mercadopago.com/checkout/preferences";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Items
        List<Map<String, Object>> items = new ArrayList<>();
        if (orderRequest.getItems() != null) {
            for (MpOrderRequest.MpOrderItem it : orderRequest.getItems()) {
                Map<String, Object> item = new HashMap<>();
                item.put("title", it.getTitle());
                item.put("quantity", it.getQuantity());
                item.put("unit_price", it.getUnitPrice());
                items.add(item);
            }
        }

        // Back URLs
        Map<String, Object> backUrls = Map.of(
                "success", Optional.ofNullable(orderRequest.getBackUrlsSuccess()).orElse("https://example.com/success"),
                "failure", Optional.ofNullable(orderRequest.getBackUrlsFailure()).orElse("https://example.com/failure"),
                "pending", Optional.ofNullable(orderRequest.getBackUrlsPending()).orElse("https://example.com/pending")
        );

        // Payer optional
        Map<String, Object> payer = new HashMap<>();
        if (orderRequest.getPayerEmail() != null) {
            payer.put("email", orderRequest.getPayerEmail());
        }

        // Body
        Map<String, Object> body = new HashMap<>();
        body.put("items", items);
        body.put("external_reference",
                orderRequest.getOrderId() != null
                        ? orderRequest.getOrderId().toString()
                        : UUID.randomUUID().toString()
        );
        body.put("back_urls", backUrls);
        if (!payer.isEmpty()) body.put("payer", payer);

        // Request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        return response.getBody() != null ? response.getBody() : new HashMap<>();
    }

    /**
     * Obtiene información de un pago en Mercado Pago (REST).
     */
    public Map<String, Object> getPayment(String paymentId) {

        String url = "https://api.mercadopago.com/v1/payments/" + paymentId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        return response.getBody() != null ? response.getBody() : new HashMap<>();
    }
}
