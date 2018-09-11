package ru.aliascage.movie_service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import ru.aliascage.movie_service.config.MovieConfig;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.springframework.util.StringUtils.isEmpty;
import static ru.aliascage.movie_service.validation.Params.SORT;

public class AvailableSortValidator implements ConstraintValidator<Available, String> {

    @Autowired
    private MovieConfig config;
    private Available constraint;

    public void initialize(Available constraint) {
        this.constraint = constraint;
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isEmpty(value)) {
            return true;
        }
        if (SORT.equals(constraint.value())) {
            return config.getAvailableSortValue().contains(value);
        } else {
            String[] params = value.split("=");
            if (params.length != 2) {
                return false;
            }
            return config.getAvailableFilterValue().contains(params[0]);
        }
    }
}
