package ru.aliascage.movie_service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PersonShort {
    private Integer id;
    private String profile_path;
    private String name;
}
