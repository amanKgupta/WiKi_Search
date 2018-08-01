package wikisearch.aman.com.wikisearch.model;

import java.io.Serializable;

/**
 * Created by Instinct on 7/31/2018.
 */

public class ContactDetails implements Serializable {
    public String imageurl;
    public String name;
    public String description;
    public String title;
    public int pageId;

    public ContactDetails() {
    }

    public ContactDetails(String imageurl, String name, String description, String title, int pageId) {
        this.imageurl = imageurl;
        this.name = name;
        this.description = description;
        this.title = title;
        this.pageId = pageId;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;

    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }
}