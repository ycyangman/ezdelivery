
package ezdelivery;

public class ReviewRegistered extends AbstractEvent {

    private Long id;
	private Long storeId;
	private Double score;

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
	
	public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}

