package ezdelivery;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Store_table")
public class Store {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id; //PK
    private String storeName;
    private String storeAddress;
    private String host;
    private String menuName;
    private Long price;
    private String status; // 판매중, 판매중지

    // 리뷰등록시 반영됨
    private Long reviewCount;
    private Double score;


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

        /*
        StoreRegistered storeRegistered = new StoreRegistered();
        BeanUtils.copyProperties(this, storeRegistered);
        storeRegistered.publishAfterCommit();

        */
    }

    @PostRemove
    public void onPostRemove(){
        System.out.println("ROOM.onPostRemove calling");

        /*
        
        StoreDeleted storeDeleted = new StoreDeleted();
        BeanUtils.copyProperties(this, storeDeleted);
        storeDeleted.publishAfterCommit();

        */

    }
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
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
    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Long getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Long reviewCount) {
        this.reviewCount = reviewCount;
    }
    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }




}
