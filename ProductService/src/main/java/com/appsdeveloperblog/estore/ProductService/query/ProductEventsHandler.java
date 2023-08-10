package com.appsdeveloperblog.estore.ProductService.query;

import com.appsdeveloperblog.estore.Core.events.ProductReservationCancelledEvent;
import com.appsdeveloperblog.estore.Core.events.ProductReservedEvent;
import com.appsdeveloperblog.estore.ProductService.core.data.ProductEntity;
import com.appsdeveloperblog.estore.ProductService.core.data.ProductRepository;
import com.appsdeveloperblog.estore.ProductService.core.events.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ProcessingGroup("product-group")
public class ProductEventsHandler {

    private ProductRepository productsRepository;

    @Autowired
    public ProductEventsHandler(ProductRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @ExceptionHandler(resultType=Exception.class)
    public void handle(Exception exception) throws Exception {
        throw exception;
    }

    @ExceptionHandler(resultType=IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception) throws Exception {
        throw exception;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) throws Exception {

        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);

        try {
            productsRepository.save(productEntity);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }


//		if(true) {
//			throw new Exception("Forcing exception in the Event Handler class");
//		}

    }

    @EventHandler
    public void on(ProductReservedEvent productReservedEvent) {

        ProductEntity productEntity =
                productsRepository.findByProductId(productReservedEvent.getProductId());

        log.debug("ProductReservedEvent: Current product quantity: " + productEntity.getQuantity());

        productEntity.setQuantity(productEntity.getQuantity()-productReservedEvent.getQuantity());

        productsRepository.save(productEntity);

        log.debug("ProductReservedEvent: New product quantity: " + productEntity.getQuantity());

        log.info("productReservedEvent is called for productId: " + productReservedEvent.getProductId() +
                " and orderId: " + productReservedEvent.getOrderId());
    }

    @EventHandler
    public void on(ProductReservationCancelledEvent productReservationCancelledEvent) {

        ProductEntity currentlyStoredProduct =
                productsRepository.findByProductId(productReservationCancelledEvent.getProductId());

        log.debug("ProductReservationCancelledEvent: Current product quantity: " +
                currentlyStoredProduct.getQuantity());

        int newQuantity =
                productReservationCancelledEvent.getQuantity() + currentlyStoredProduct.getQuantity();

        currentlyStoredProduct.setQuantity(newQuantity);

        productsRepository.save(currentlyStoredProduct);

        log.debug("ProductReservationCancelledEvent: New product quantity: " +
                currentlyStoredProduct.getQuantity());
    }

    @ResetHandler
    public void reset() {

        productsRepository.deleteAll();
    }
}