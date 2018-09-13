package ru.aliascage.movie_service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Setter
@Getter
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoteAverageResponse implements Serializable {
    private Float voteAverage;
    private LocalDateTime lastUpdate;
    private Float percent;
    private VoteAverageStatus status = VoteAverageStatus.START;

    @JsonProperty
    public boolean isActual() {
        return lastUpdate != null && lastUpdate.until(LocalDateTime.now(), ChronoUnit.MINUTES) <= 5;
    }
}
