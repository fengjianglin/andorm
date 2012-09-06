
package com.ifatter.andorm.model;

import com.ifatter.andorm.orm.Column;
import com.ifatter.andorm.orm.Id;
import com.ifatter.andorm.orm.Table;

@Table(name = "table_book")
public class Book {

    @Id
    @Column(name = "_id")
    private int id;

    @Column(name = "title", length = 64)
    private String title;

    @Column(name = "url", length = 64)
    private String url;

    @Column(name = "icon_url", length = 64)
    private String iconUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

}
