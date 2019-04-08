package com.aqr.etf.book.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Data
@Entity(name = "order_book")
public class OrderBook implements IModel {

    @Id
    private UUID orderId;
    private Symbol symbol;
    private Double limitPrice;
    private Side side;
    private Long quantity;
    private Long changeInQuantity;
}
