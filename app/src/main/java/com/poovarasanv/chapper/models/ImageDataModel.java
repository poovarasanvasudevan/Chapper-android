package com.poovarasanv.chapper.models;

/**
 * Created by poovarasanv on 2/8/16.
 */
public class ImageDataModel {

    private String imageTitle , imagePath;

    /**
     * @return the imageTitle
     */
    public String getImageTitle() {
        return imageTitle;
    }

    /**
     * @param imageTitle the imageTitle to set
     */
    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    /**
     * @return the imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * @param imagePath the imagePath to set
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ImageDataModel(String imageTitle, String imagePath) {
        super();
        this.imageTitle = imageTitle;
        this.imagePath = imagePath;
    }

}