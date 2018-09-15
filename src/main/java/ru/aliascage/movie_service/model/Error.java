package ru.aliascage.movie_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@Setter
@Getter
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Error {
    private String code;
    private String message;
}
