package ezdelivery;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="Mypage_table")
public class Mypage {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private Long storeId;
        private String storeName;
        private String menuName;
        private String guestName;
        private String orderDateTime;
        private String guestAddress;
        private Long orderId;
        private Double payAmt;
        private String payDate;
        private String host;
        private Long points;
        private String status;
        private Double price;
        private Long orderNumber;

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

        public String getMenuName() {
            return menuName;
        }

        public void setMenuName(String menuName) {
            this.menuName = menuName;
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
        public String getGuestAddress() {
            return guestAddress;
        }

        public void setGuestAddress(String guestAddress) {
            this.guestAddress = guestAddress;
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
        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }
        public Long getPoints() {
            return points;
        }

        public void setPoints(Long points) {
            this.points = points;
        }
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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

        @Override
        public String toString() {
            return "Mypage [guestAddress=" + guestAddress + ", guestName=" + guestName + ", host=" + host + ", id=" + id
                    + ", menuName=" + menuName + ", orderDateTime=" + orderDateTime + ", orderId=" + orderId
                    + ", orderNumber=" + orderNumber + ", payAmt=" + payAmt + ", payDate=" + payDate + ", points="
                    + points + ", price=" + price + ", status=" + status + ", storeId=" + storeId + ", storeName="
                    + storeName + "]";
        }


}
