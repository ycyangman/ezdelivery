package ezdelivery;

import ezdelivery.config.kafka.KafkaProcessor;

import java.util.Optional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired StoreRepository storeRepository;

    private Store getStoreByStoreId(long storeId) {
			
		Optional<Store> stores = storeRepository.findById(storeId);

		if(!stores.isPresent()) {

			System.out.println("\n\n##### Store table no data found by : " + storeId + "\n\n");
			return null;

		}

		return stores.get();
	}

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReviewRegistered_UpdateReview(@Payload ReviewRegistered reviewRegistered){

        if(!reviewRegistered.validate()) return;

        System.out.println("\n\n##### listener UpdateReview : " + reviewRegistered.toJson() + "\n\n");

   		// ----------------------------------------
		// 리뷰 등록 시 -> Room의 리뷰 카운트 증가
		//-----------------------------------------

		long storeId = reviewRegistered.getStoreId()== null ? 0 : reviewRegistered.getStoreId(); // 등록된 리뷰의 RoomID
		double score = reviewRegistered.getScore() == null ? 0 : reviewRegistered.getScore();
		
		Store store = getStoreByStoreId(storeId);
		if(store == null) {
			return;
		}

		long reviewCnt = (store.getReviewCount() == null ?0 : store.getReviewCount()) +1;
		store.setReviewCount(reviewCnt); // 리뷰건수 증가/감소
		
		double storeScore = store.getScore() == null ?0 : store.getScore();
		
		if( score >0) {
			
			score = (storeScore + score)/reviewCnt;
			store.setScore(score);
		}

        storeRepository.save(store);
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
