package com.interswitch.bookstore.services;

import com.interswitch.bookstore.dtos.AddToCartDTO;
import com.interswitch.bookstore.dtos.PurchaseHistory;
import com.interswitch.bookstore.exceptions.AuthenticationException;
import com.interswitch.bookstore.exceptions.Messages;
import com.interswitch.bookstore.models.*;
import com.interswitch.bookstore.repositories.ShoppingCartRepository;
import com.interswitch.bookstore.utils.BasicUtil;
import com.interswitch.bookstore.utils.api.PaginateApiResponse;
import com.interswitch.bookstore.utils.api.PaginationBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository cartRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, ShoppingCart> redisTemplate;

    public ShoppingCart saveShoppingCart(ShoppingCart shoppingCart){

        return cartRepository.save(shoppingCart);
    }

    public ShoppingCart addItemsToCart(AddToCartDTO addToCartDTO){
        ShoppingCart cart = addToCartDTO.getShoppingCart();
        List<CartItem> cartItems = addToCartDTO.getCartItems();

        if(null == cart.getCartItems() || cart.getCartItems().isEmpty()){
            cart.setCartItems(cartItems);
        }else{
            cart.setCartItems(mergeCartItems(cart.getCartItems(), cartItems));
        }

        return cart;
    }


    public Boolean cacheOngoingShopping(ShoppingCart shoppingCart){
        User shopper = userService.getAuthUser();
        if(null == shopper){
            throw new AuthenticationException(Messages.NO_AUTH_USER);
        }
        String id = shopper.getId().toString();
        log.info("Caching shopping cart for {} ",shopper.getUsername());
        redisTemplate.opsForValue().set("shoppingcart:" + id, shoppingCart);

        return true;
    }

    public PaginateApiResponse findUserPurchaseHistory(Pageable pageable) {
        User user = userService.getAuthUser();
        Page<ShoppingCart> shoppingCartsPage = cartRepository.findByStatusAndUser(CartStatus.PROCESSED, user, pageable);

        List<PurchaseHistory> histories = shoppingCartsPage.getContent().stream()
                .map(shoppingCart -> {
                    PurchaseHistory purchaseHistory = new PurchaseHistory();
                    purchaseHistory.setAmount(shoppingCart.calculateShoppingCost(false));
                    purchaseHistory.setPurchaseDate(shoppingCart.getDatePaid());
                    purchaseHistory.setPaymentOption(shoppingCart.getPaymentOption());

                    Map<String, Integer> books = shoppingCart.getCartItems().stream()
                            .collect(Collectors.toMap(
                                    item -> {
                                        Book book = item.getBook();
                                        return book.getTitle() + " By " + book.getAuthor().getName() + ", " + book.getYearOfPublication();
                                    },
                                    CartItem::getQuantity
                            ));

                    purchaseHistory.setBooks(books);
                    return purchaseHistory;
                })
                .collect(Collectors.toList());

        Page<PurchaseHistory> historyPage = new PageImpl<>(histories, pageable, shoppingCartsPage.getTotalElements());
        PaginationBody<PurchaseHistory> body = new PaginationBody(historyPage, pageable.getPageSize());

        log.info("Retrieved purchased history for {} ",user.getUsername());
        return new PaginateApiResponse<PurchaseHistory>(body);

    }

    @Cacheable(value = "shoppingCartCache", key = "'shoppingcart:' + #key")
    public ShoppingCart retrieveCachedCart() {

        User shopper = userService.getAuthUser();
        log.info("Cached cart retrieve for {} ",shopper.getUsername());
        if(null == shopper){
            throw new AuthenticationException(Messages.NO_AUTH_USER);
        }
        String id = shopper.getId().toString();

        if(redisTemplate.hasKey("shoppingcart:"+id)){
            return redisTemplate.opsForValue().get("shoppingcart:"+id);

        }

        return new ShoppingCart();

    }

    public Page<ShoppingCart> findPendingShopping(Pageable pageable){
        return this.cartRepository.findByStatus(CartStatus.PENDING, pageable);
    }

    public ShoppingCart getByReference(String ref){
        log.info("Retrieved shopping cart with reference  ",ref);
        if(!BasicUtil.validString(ref)){
            throw new IllegalStateException("Reference is required");
        }
        ShoppingCart shoppingCart = cartRepository.findByTransactionReference(ref);
        if(null == ref)
            throw new IllegalStateException("No shopping cart found");
        return shoppingCart;
    }

    private List<CartItem> mergeCartItems(List<CartItem> oldItems, List<CartItem> newItems) {
        List<CartItem> mergedItems = new ArrayList<>(oldItems);

        for (CartItem newItem : newItems) {
            if (mergedItems.contains(newItem)) {
                int index = mergedItems.indexOf(newItem);
                CartItem oldItem = mergedItems.get(index);
                oldItem.setQuantity(oldItem.getQuantity() + newItem.getQuantity());
                mergedItems.set(index, oldItem);
            } else {
                mergedItems.add(newItem);
            }
        }

        return mergedItems;
    }

}
