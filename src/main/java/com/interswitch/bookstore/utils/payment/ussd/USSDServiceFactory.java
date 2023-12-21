package com.interswitch.bookstore.utils.payment.ussd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class USSDServiceFactory {

    private Map<String, USSDServiceInterface> serviceMap = new HashMap<>();

    @Value("${services.ussd.id}")
    private String serviceId;

    @Autowired
    public USSDServiceFactory(List<USSDServiceInterface> gateways) {
        gateways.forEach(this::register);
    }

    private void register(USSDServiceInterface gatewayInterface) {
        serviceMap.put(gatewayInterface.getServiceId(), gatewayInterface);
    }

    public USSDServiceInterface getInstance() {
        return serviceMap.get(serviceId);
    }
}
