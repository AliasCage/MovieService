package ru.aliascage.movie_service.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Genre implements Serializable {
    private Integer id;
    private String name;
}
