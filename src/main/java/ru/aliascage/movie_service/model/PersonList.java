package ru.aliascage.movie_service.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PersonList extends PagedList {
    List<PersonShort> results;
}
