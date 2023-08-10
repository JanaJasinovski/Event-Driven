package com.appsdeveloperblog.estore.ProductService.command.rest;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRestModule {

//    @NotBlank(message = "Product title is a required field")
    private String title;

    @Min(value = 1, message = "Price cannot be lower that 1")
    private BigDecimal price;

    @Min(value = 1, message = "Quantity cannot be lower that 1")
    @Max(value = 5, message = "Quantity cannot be larger that 1")
    private Integer quantity;
}
