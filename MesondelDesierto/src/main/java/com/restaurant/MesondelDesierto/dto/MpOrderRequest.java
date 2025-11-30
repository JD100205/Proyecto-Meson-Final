package com.restaurant.MesondelDesierto.dto;

import java.util.List;

public class MpOrderRequest {
    private Long orderId;
    private String payerEmail;
    private List<MpOrderItem> items;
    private String backUrlsSuccess;
    private String backUrlsFailure;
    private String backUrlsPending;

    public static class MpOrderItem {
        private String title;
        private Integer quantity;
        private Double unitPrice;
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getPayerEmail() { return payerEmail; }
    public void setPayerEmail(String payerEmail) { this.payerEmail = payerEmail; }
    public List<MpOrderItem> getItems() { return items; }
    public void setItems(List<MpOrderItem> items) { this.items = items; }
    public String getBackUrlsSuccess() { return backUrlsSuccess; }
    public void setBackUrlsSuccess(String backUrlsSuccess) { this.backUrlsSuccess = backUrlsSuccess; }
    public String getBackUrlsFailure() { return backUrlsFailure; }
    public void setBackUrlsFailure(String backUrlsFailure) { this.backUrlsFailure = backUrlsFailure; }
    public String getBackUrlsPending() { return backUrlsPending; }
    public void setBackUrlsPending(String backUrlsPending) { this.backUrlsPending = backUrlsPending; }
}

