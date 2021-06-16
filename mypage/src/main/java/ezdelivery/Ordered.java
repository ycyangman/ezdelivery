package ezdelivery;

public class Ordered extends AbstractEvent {

    private Long id;

    private Long storeId;
    private String storeName;
    private Double price;
    private Long orderNumber;
    private String guestName;
    private String orderDateTime;
    private String status;
    private String host;
    private String menuName;
    private String guestAddress;

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
    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }
    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }
    public String getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
    public String getGuestAddress() {
        return guestAddress;
    }

    public void setGuestAddress(String guestAddress) {
        this.guestAddress = guestAddress;
    }    

    @Override
    public String toString() {
        return "Ordered [guestAddress=" + guestAddress + ", guestName=" + guestName + ", host=" + host + ", id=" + id
                + ", menuName=" + menuName + ", orderDateTime=" + orderDateTime + ", orderNumber=" + orderNumber
                + ", price=" + price + ", status=" + status + ", storeId=" + storeId + ", storeName=" + storeName + "]";
    }
}