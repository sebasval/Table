package tesis.utadeo.table;

/**
 * Created by Administrador on 19/05/2017.
 */

public class Blog_Card {

    private String title, desc, image, uid;

    public Blog_Card() {
    }

    public Blog_Card(String title, String desc, String image, String uid) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
