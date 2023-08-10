package com.appsdeveloperblog.estore.ProductService.command;

import com.appsdeveloperblog.estore.Core.commands.ReserveProductCommand;
import com.appsdeveloperblog.estore.Core.events.ProductReservedEvent;
import com.appsdeveloperblog.estore.ProductService.core.events.ProductCreatedEvent;
import com.appsdeveloperblog.estore.Core.commands.CancelProductReservationCommand;
import com.appsdeveloperblog.estore.Core.events.ProductReservationCancelledEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;


@Aggregate(snapshotTriggerDefinition ="productSnapshotTriggerDefinition")
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;

    private String title;
    private BigDecimal price;
    private Integer quantity;

    public ProductAggregate() {

    }

    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand) {

        if(createProductCommand.getPrice().compareTo(BigDecimal.ZERO)<=0) {
            throw new IllegalArgumentException("Price cannot be less or equal to zero.");
        }

        if(createProductCommand.getTitle()==null || createProductCommand.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent();

        BeanUtils.copyProperties(createProductCommand, productCreatedEvent);

        AggregateLifecycle.apply(productCreatedEvent);

    }


    @CommandHandler
    public void handle(ReserveProductCommand reserveProductCommand) {

        if(quantity<reserveProductCommand.getQuantity()) {
            throw new IllegalArgumentException("Insufficent number of items in stock");
        }

        ProductReservedEvent productReservedEvent = ProductReservedEvent.builder()
                .orderId(reserveProductCommand.getOrderId())
                .productId(reserveProductCommand.getProductId())
                .quantity(reserveProductCommand.getQuantity())
                .userId(reserveProductCommand.getUserId())
                .build();

        AggregateLifecycle.apply(productReservedEvent);

    }


    @CommandHandler
    public void handle(CancelProductReservationCommand cancelProductReservationCommand) {

        ProductReservationCancelledEvent  productReservationCancelledEvent =
                ProductReservationCancelledEvent.builder()
                        .orderId(cancelProductReservationCommand.getOrderId())
                        .productId(cancelProductReservationCommand.getProductId())
                        .quantity(cancelProductReservationCommand.getQuantity())
                        .reason(cancelProductReservationCommand.getReason())
                        .userId(cancelProductReservationCommand.getUserId())
                        .build();

        AggregateLifecycle.apply(productReservationCancelledEvent);

    }

    @EventSourcingHandler
    public void on(ProductCreatedEvent productCreatedEvent) {

        this.productId = productCreatedEvent.getProductId();
        this.price = productCreatedEvent.getPrice();
        this.title = productCreatedEvent.getTitle();
        this.quantity = productCreatedEvent.getQuantity();

    }


    @EventSourcingHandler
    public void on(ProductReservedEvent productReservedEvent) {
        this.quantity -= productReservedEvent.getQuantity();

    }


    @EventSourcingHandler
    public void on(ProductReservationCancelledEvent  productReservationCancelledEvent) {
        this.quantity += productReservationCancelledEvent.getQuantity();
    }

}