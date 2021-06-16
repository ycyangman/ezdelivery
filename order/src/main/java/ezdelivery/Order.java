package ezdelivery;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
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

    @PostPersist
    public void onPostPersist(){

        ezdelivery.external.Payment payment = new ezdelivery.external.Payment();
        BeanUtils.copyProperties(this, payment);
        payment.setOrderId(getId());

        payment.setPayAmt(getPrice() * getOrderNumber());
        payment.setPayDate(new SimpleDateFormat("YYYYMMdd").format(new Date()));
        payment.setStatus("결재승인");

        try {
        
            OrderApplication.applicationContext.getBean(ezdelivery.external.PaymentService.class).makePay(payment);
        
        }catch(Exception e) {
            throw new RuntimeException("결제서비스 호출 실패입니다."+e.getLocalizedMessage());

            //e.printStackTrace();
        }

        // ----------------------------------------------------
        // 결제까지 완료되면 최종적으로 예약 완료 이벤트 발생
        // ----------------------------------------------------
        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);
        ordered.setStatus("결재승인");
        ordered.publishAfterCommit();

    }

    //해당 엔티티를 업데이트 한 이후
    @PostUpdate
    public void onPostUpdate(){

        System.out.println("\n==============Order.onPostUpdate calling=================\n");

        OrderCanceled orderCanceled = new OrderCanceled();
        BeanUtils.copyProperties(this, orderCanceled);
        orderCanceled.setStoreId(getId());
        orderCanceled.setStatus("주문취소");
        orderCanceled.publishAfterCommit();

    }

    //해당 엔티티를 삭제한 이후
    @PostRemove
    public void onPostRemove(){

        OrderCanceled orderCanceled = new OrderCanceled();
        BeanUtils.copyProperties(this, orderCanceled);
        orderCanceled.setStoreId(getId());
        orderCanceled.setStatus("주문취소");
        orderCanceled.publishAfterCommit();
    }

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

}
