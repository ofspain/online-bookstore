package com.interswitch.bookstore.utils.payment.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PaymentGatewayServiceFactory {

    private Map<String, PaymentGatewayInterface> serviceMap = new HashMap<>();

    //payment gateway in use.
    @Value("${services.payment.gateway.id}")
    private String serviceId;

    @Autowired
    public PaymentGatewayServiceFactory(List<PaymentGatewayInterface> gateways) {
        gateways.forEach(this::register);
    }

    private void register(PaymentGatewayInterface gatewayInterface) {
        serviceMap.put(gatewayInterface.getServiceId(), gatewayInterface);
    }

    public PaymentGatewayInterface getInstance() {
        return serviceMap.get(serviceId);
    }
}
