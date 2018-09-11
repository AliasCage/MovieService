package ru.aliascage.movie_service.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import ru.aliascage.movie_service.config.MovieConfig;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;
import static ru.aliascage.movie_service.validation.Params.SORT;

public class AvailableSortValidator implements ConstraintValidator<Available, String> {

    @Autowired
    private MovieConfig config;
    private Available constraint;

    public void initialize(Available constraint) {
        this.constraint = constraint;
    }

    public boolean isValid(String sortValue, ConstraintValidatorContext context) {
        if (isEmpty(sortValue)) {
            return true;
        }
        List<String> checkList = SORT.equals(constraint.value())
                ? config.getAvailableSortValue()
                : config.getAvailableFilterValue();
        return checkList.stream().anyMatch(sortValue::equalsIgnoreCase);
    }
}
