package es.source.code.factory;

import es.source.code.model.Food;
import es.source.code.model.FoodType;

public class ColdFoodFactory implements FoodFactory{
    @Override
    public Food CreateFood(String food_name, double food_price, int food_img) {
        Food food=new Food();
        food.setFood_name(food_name);
        food.setFood_img(food_img);
        food.setFood_price(food_price);
        food.setFood_type(FoodType.ColdFood);
        food.setFood_order_time(0);
        food.setFood_note("");
        food.setFood_hasorder(false);
        food.setFood_reserve(10);
        return food;
    }
}
