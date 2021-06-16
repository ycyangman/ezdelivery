package ezdelivery;

public class PayCanceled extends AbstractEvent {

    private Long id;
    private Long storeId;
    private String storeName;    
    private String menuName;
    private Double payAmt;
    private String payDate;
    private String status;
    private Long orderId;
    private Long orderNumber;
    private String guestName;

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
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public Double getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(Double payAmt) {
        this.payAmt = payAmt;
    }
    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public String toString() {
        return "PayCanceled [guestName=" + guestName + ", id=" + id + ", menuName=" + menuName + ", orderId=" + orderId
                + ", orderNumber=" + orderNumber + ", payAmt=" + payAmt + ", payDate=" + payDate + ", status=" + status
                + ", storeId=" + storeId + ", storeName=" + storeName + "]";
    }

    
}