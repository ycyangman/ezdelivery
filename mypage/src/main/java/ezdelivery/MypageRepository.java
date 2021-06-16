package ezdelivery;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

    /*
    public interface CrudRepository<T, ID extends Serializable> extends Repository<T, ID> {
    <S extends T> S save(S var1);
    <S extends T> Iterable<S> save(Iterable<S> var1);
 
    T findOne(ID var1);
    
    <T> findAll();
    Iterable<T> findAll(Iterable<ID> var1);
 
    boolean exists(ID var1);
    long count();
 
    void delete(ID var1);
    void delete(T var1);
    void delete(Iterable<? extends T> var1);
    void deleteAll();
*/


public interface MypageRepository extends CrudRepository<Mypage, Long> {

    List<Mypage> findByStoreId(Long storeId);

    List<Mypage> findByOrderId(Long orderId);

    List<Mypage> findByGuestName(String guestName);

    List<Mypage> findByHost(String host);

}