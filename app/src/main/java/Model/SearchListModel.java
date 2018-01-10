package Model;

import java.io.Serializable;

/**
 * Created by Ajay on 03/01/2018.
 */

public class SearchListModel implements Serializable{
    String off_percentage,title,new_price,old_price,image;

    public SearchListModel() {
    }

    public String get_off_percentage() {
        return off_percentage;
    }
    public void set_off_percentage(String off_percentage) {
        this.off_percentage = off_percentage;
    }

    public String get_title() {
        return title;
    }
    public void set_title(String title) {
        this.title = title;
    }

    public String get_new_price() {
        return new_price;
    }
    public void set_new_price(String new_price) {
        this.new_price = new_price;
    }

    public String get_old_price() {
        return old_price;
    }
    public void set_old_price(String old_price) {
        this.old_price = old_price;
    }

    public String get_image() {
        return image;
    }
    public void set_image(String image) {
        this.image = image;
    }
}
