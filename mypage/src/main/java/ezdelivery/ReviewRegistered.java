
package ezdelivery;

public class ReviewRegistered extends AbstractEvent {

    private Long id;
    private Long storeId;
    private String guestName;
    private Integer score;
    private String reviewContents;
    private String orderDateTime;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getStoreId() {
        return storeId;
    }
    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }
    public String getGuestName() {
        return guestName;
    }
    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }
    public Integer getScore() {
        return score;
    }
    public void setScore(Integer score) {
        this.score = score;
    }
    public String getReviewContents() {
        return reviewContents;
    }
    public void setReviewContents(String reviewContents) {
        this.reviewContents = reviewContents;
    }

    
    public String getOrderDateTime() {
        return orderDateTime;
    }
    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
    }
    @Override
    public String toString() {
        return "ReviewRegistered [guestName=" + guestName + ", id=" + id + ", orderDateTime=" + orderDateTime
                + ", reviewContents=" + reviewContents + ", score=" + score + ", storeId=" + storeId + "]";
    }

}

