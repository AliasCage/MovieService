package ru.aliascage.movie_service.validation;

import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;
import static ru.aliascage.movie_service.validation.Params.SORT;

public class AvailableSortValidator implements ConstraintValidator<Available, String> {

    private Available constraint;

    @Value("#{'${available.sort.value}'.split(',')}")
    private List<String> availableSortValue;
    @Value("#{'${available.filter.value}'.split(',')}")
    private List<String> availableFilterValue;

    public void initialize(Available constraint) {
        this.constraint = constraint;
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isEmpty(value)) {
            return true;
        }
        if (SORT.equals(constraint.value())) {
            return availableSortValue.contains(value);
        } else {
            String[] params = value.split("=");
            if (params.length != 2) {
                return false;
            }
            return availableFilterValue.contains(params[0]);
        }
    }
}
