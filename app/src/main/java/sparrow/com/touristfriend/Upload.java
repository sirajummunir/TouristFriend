package sparrow.com.touristfriend;

public class Upload {
    private String placeName,address;
    private String imageUrl;

    public Upload() {

    }

    public Upload(String placeName, String address, String imageUrl) {
        this.placeName = placeName;
        this.address = address;
        this.imageUrl = imageUrl;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
