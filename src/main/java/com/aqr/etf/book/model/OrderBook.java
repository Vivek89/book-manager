package com.aqr.etf.book.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Data
@Entity(name = "order_book")
public class OrderBook implements IModel, Comparable<OrderBook> {

    @Id
    private UUID orderId;
    private Symbol symbol;
    private Double limitPrice;
    private Side side;
    private Long quantity;

    // delta change in the order-id for modify and cancellation
    private Long changeInQuantity;

    // Descending Compare
    @Override
    public int compareTo(@NotNull OrderBook o) {
        if (this.limitPrice > o.limitPrice) {
            return -1;
        }
        return 1;
    }
}
