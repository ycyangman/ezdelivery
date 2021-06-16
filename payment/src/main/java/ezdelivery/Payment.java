package ezdelivery;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)

    private Long id;
    private Long storeId;  //상점ID
    private String storeName; //상점명
    private String host; //점주
    private String menuName; //메뉴명
    private Double payAmt; //결제금액
    private String payDate; //결제일자
    private String status; //상태
    private Long orderId; //주문ID
    private Long orderNumber; //주문건수
    private String guestName; //고객명


    /*
    
    @PostLoad: 해당 엔티티를 새로 불러오거나 refresh 한 이후.
    @PrePersist: 해당 엔티티를 저장하기 이전
    @PostPersist: 해당 엔티티를 저장한 이후
    @PreUpdate: 해당 엔티티를 업데이트 하기 이전
    @PostUpdate: 해당 엔티티를 업데이트 한 이후
    @PreRemove: 해당 엔티티를 삭제하기 이전
    @PostRemove: 해당 엔티티를 삭제한 이후
    */

    @PostPersist
    public void onPostPersist(){
        
        
        PayApproved payApproved = new PayApproved();
        BeanUtils.copyProperties(this, payApproved);
        payApproved.setStatus("결재승인");
        payApproved.publishAfterCommit();

        // 시간 끌기
        /*
        try {
            
            System.out.println("\n\n !!!!!!!!!!!!!!! PAYMENT SYSTEM BREAKING !!!!!!!!!!!!!!!!!!!!! \n\n");

            Thread.currentThread().sleep((long) (400 + Math.random() * 220));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        
        System.out.println("\n\n !!!!!!!!!!!!!!! PAYMENT COMPLETED !!!!!!!!!!!!!!!!!!!!! \n\n");

    }

    //해당 엔티티를 업데이트 한 이후
    @PostUpdate
    public void onPostUpdate(){
        System.out.println("\n===Payment.onPostUpdate calling==\n");
        PayCanceled payCanceled = new PayCanceled();
        BeanUtils.copyProperties(this, payCanceled);
        payCanceled.setStatus("결재취소");
        
        payCanceled.publishAfterCommit();

        System.out.println("======================================================================================");
        System.out.println("\n\n##### onPostUpdate action to KAFKA : " + payCanceled.toString() + "\n\n");
        System.out.println("======================================================================================");
    }

            
    //해당 엔티티를 삭제한 이후
    @PostRemove
    public void onPostRemove(){
        System.out.println("Payment.onPostRemove calling");
        PayCanceled payCanceled = new PayCanceled();
        BeanUtils.copyProperties(this, payCanceled);
        payCanceled.setStatus("결재취소");
        
        payCanceled.publishAfterCommit();

        System.out.println("======================================================================================");
        System.out.println("\n\n##### onPostRemove action to KAFKA : " + payCanceled.toString() + "\n\n");
        System.out.println("======================================================================================");
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
    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

}
