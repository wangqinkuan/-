package es.source.code.factory;

import es.source.code.model.Food;

public interface FoodFactory {
    Food CreateFood(String food_name,double food_price,int food_img);
}
