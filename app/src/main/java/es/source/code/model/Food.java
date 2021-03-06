package es.source.code.model;



public class Food {

    private String food_name;
    private double food_price;
    private int food_img;
    private int food_type;
    //备注
    private String food_note="";
    private int food_order_button;
    //被点了几次
    private int food_order_time;
    //是否下单
    private boolean food_hasorder;
    //存量
    private int food_reserve;

    public int getFood_reserve() {
        return food_reserve;
    }

    public void setFood_reserve(int food_reserve) {
        this.food_reserve = food_reserve;
    }



    public boolean isFood_hasorder() {
        return food_hasorder;
    }

    public void setFood_hasorder(boolean food_hasorder) {
        this.food_hasorder = food_hasorder;
    }




    public String getFood_note() {
        return food_note;
    }

    public void setFood_note(String food_note) {
        this.food_note = food_note;
    }


    public int getFood_order_time() {
        return food_order_time;
    }

    public void setFood_order_time(int food_order_time) {
        this.food_order_time = food_order_time;
    }



    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public double getFood_price() {
        return food_price;
    }

    public void setFood_price(double food_price) {
        this.food_price = food_price;
    }

    public int getFood_img() {
        return food_img;
    }

    public void setFood_img(int food_img) {
        this.food_img = food_img;
    }

    public int getFood_type() {
        return food_type;
    }

    public void setFood_type(int food_type) {
        this.food_type = food_type;
    }

    public int getFood_order_button() {
        return food_order_button;
    }

    public void setFood_order_button(int food_order_button) {
        this.food_order_button = food_order_button;
    }


}
