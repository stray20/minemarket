package hu.infinityhosting.minemarket;

/**
 * Created by kg4b0r on 15. 02. 02..
 */
public class SMS {
    private long id;
    private String date;
    private String tel;
    private String text;
    private String price;
    private long status; // 1 - delivered    2 - only readed

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }


    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getTel(){
        return tel;
    }

    public void setTel(String tel){
        this.tel = tel;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getPrice(){
        return price;
    }

    public void setPrice(String price){
        this.price = price;
    }

    public long getStatus(){
        return status;
    }

    public void setStatus(long status){
        this.status = status;
    }

    @Override
    public String toString(){
        return text;
    }




}
