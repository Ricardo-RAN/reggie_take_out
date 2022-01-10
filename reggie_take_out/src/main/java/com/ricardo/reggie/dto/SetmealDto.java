package com.ricardo.reggie.dto;

import com.ricardo.reggie.domain.Setmeal;
import com.ricardo.reggie.domain.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
