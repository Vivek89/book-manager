package com.aqr.etf.book.model.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Level {
    private String level;
    private Double price;
    private Long quantity;
}
