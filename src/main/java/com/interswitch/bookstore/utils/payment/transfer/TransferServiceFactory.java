package com.interswitch.bookstore.utils.payment.transfer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TransferServiceFactory{

    private Map<String, TransferServiceInterface> serviceMap = new HashMap<>();

    @Value("${services.transfer.id}")
    private String serviceId;

    @Autowired
    public TransferServiceFactory(List<TransferServiceInterface> gateways) {
        gateways.forEach(this::register);
    }

    private void register(TransferServiceInterface gatewayInterface) {
        serviceMap.put(gatewayInterface.getServiceId(), gatewayInterface);
    }

    public TransferServiceInterface getInstance() {
        return serviceMap.get(serviceId);
    }
}
