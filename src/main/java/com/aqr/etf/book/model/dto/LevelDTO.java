package com.aqr.etf.book.model.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class LevelDTO {
    private List<Level> buyLevel;
    private List<Level> sellLevel;

}

