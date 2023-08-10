package com.appsdeveloperblog.estore.ProductService.command.rest;

import com.appsdeveloperblog.estore.ProductService.command.CreateProductCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping( "/products" )
@RequiredArgsConstructor
public class ProductsCommandController {

    private final Environment env;
    private final CommandGateway commandGateway;

    @PostMapping
    public String createProduct(@Valid @RequestBody CreateProductRestModule createProductRestModule) {
        CreateProductCommand createProductCommand = CreateProductCommand.builder()
                .price(createProductRestModule.getPrice())
                .quantity(createProductRestModule.getQuantity())
                .title(createProductRestModule.getTitle())
                .productId(UUID.randomUUID().toString())
                .build();

        String returnValue = commandGateway.sendAndWait(createProductCommand);
//        try {
//            returnValue = commandGateway.sendAndWait(createProductCommand);
//        } catch (Exception ex) {
//            returnValue = ex.getLocalizedMessage();
//        }

        return returnValue;
    }

//    @GetMapping
//    public String getProduct() {
//        return "HTTP POST Handled " + env.getProperty("local.server.port");
//    }
//
//    @PutMapping
//    public String updateProduct() {
//        return "HTTP POST Handled";
//    }
//
//    @DeleteMapping
//    public String deleteProduct() {
//        return "HTTP POST Handled";
//    }
}
