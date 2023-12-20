package com.interswitch.bookstore.services;

import com.interswitch.bookstore.dtos.AddToCartDTO;
import com.interswitch.bookstore.dtos.PurchaseHistory;
import com.interswitch.bookstore.models.*;
import com.interswitch.bookstore.repositories.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository cartRepository;

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
        String id = shoppingCart.getUser().getId().toString();
        redisTemplate.opsForValue().set("shoppingcart:" + id, shoppingCart);

        return false;
    }

    public Page<PurchaseHistory> findUserPurchaseHistory(User user, Pageable pageable) {
        Page<ShoppingCart> shoppingCartsPage = cartRepository.findByStatusAndUser(CartStatus.processed, user, pageable);

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

        return new PageImpl<>(histories, pageable, shoppingCartsPage.getTotalElements());
    }

    @Cacheable(value = "shoppingCartCache", key = "'shoppingcart:' + #key")
    public ShoppingCart retrieveCachedCart(User user) {
        String id = user.getId().toString();

        if(redisTemplate.hasKey("shoppingcart:"+id)){
            return redisTemplate.opsForValue().get("shoppingcart:"+id);

        }

        return new ShoppingCart();

    }

    private List<CartItem> mergeCartItems(List<CartItem> oldItems,List<CartItem> newItems){
        for(CartItem newItem : newItems){
            if(oldItems.contains(newItem)){
                int index = oldItems.indexOf(newItem);
                CartItem oldItem = oldItems.get(index);
                oldItem.setQuantity(oldItem.getQuantity() + newItem.getQuantity());
                oldItems.set(index,oldItem);
            }else{
                oldItems.add(newItem);
            }
        }

        return oldItems;
    }
}
