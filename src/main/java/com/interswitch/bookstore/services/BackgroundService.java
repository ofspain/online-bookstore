package com.interswitch.bookstore.services;

import com.interswitch.bookstore.models.ShoppingCart;
import com.interswitch.bookstore.utils.payment.PaymentChoiceFactory;
import com.interswitch.bookstore.utils.payment.PaymentOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@EnableScheduling
@Component
public class BackgroundService {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private PaymentChoiceFactory choiceFactory;

    @Scheduled(fixedDelayString = "${cron.run.after.every}")
    public void queryPendingPayment() {
        int pageNumber = 0;
        final int pageSize = 500;
        PageRequest pageRequest;
        Page<ShoppingCart> pendings;

        do {
            pageRequest = PageRequest.of(pageNumber, pageSize);
            pendings = shoppingCartService.findPendingShopping(pageRequest);
            List<ShoppingCart> pendingCarts = pendings.getContent();

            pendingCarts.parallelStream().forEach(shoppingCart -> {
                PaymentOption option = shoppingCart.getPaymentOption();

                switch (option) {
                    case TRANSFER:
                        processTransferPayment(shoppingCart);
                        break;
                    case USSD:
                        processUSSDPayment(shoppingCart);
                        break;
                    case WEB:
                        processWebPayment(shoppingCart);
                        break;
                }
            });

            pageNumber++;
        } while (!pendings.isEmpty());
    }

    private void processTransferPayment(ShoppingCart shoppingCart) {
        choiceFactory.getPaymentChoiceImplementation(PaymentOption.TRANSFER).requery(shoppingCart);
    }

    private void processUSSDPayment(ShoppingCart shoppingCart) {
        choiceFactory.getPaymentChoiceImplementation(PaymentOption.USSD).requery(shoppingCart);
    }

    private void processWebPayment(ShoppingCart shoppingCart) {
        choiceFactory.getPaymentChoiceImplementation(PaymentOption.WEB).requery(shoppingCart);
    }

}

