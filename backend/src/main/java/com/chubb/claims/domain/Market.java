package com.chubb.claims.domain;

public enum Market {
    AU("AUD"), SG("SGD"), HK("HKD"), MY("MYR"), TH("THB"), VN("VND");
    private final String currency;

    Market(String currency) {
        this.currency = currency;
    }

    public String currency() {
        return currency;
    }
}
