package ezdelivery;

import ezdelivery.config.kafka.KafkaProcessor;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MypageViewHandler {


    @Autowired
    private MypageRepository mypageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrdered_then_CREATE_1 (@Payload Ordered ordered) {
        try {

            if (!ordered.validate()) return;

            System.out.println("\n\n##### whenOrdered : " + ordered.toString() + "\n\n");

            // view 객체 생성
            Mypage mypage = new Mypage();

            BeanUtils.copyProperties(ordered, mypage);

            mypage.setOrderId(ordered.getId());

            // view 레파지 토리에 save
            mypageRepository.save(mypage);

            System.out.println("\n\n##### mypageRepository save : " + mypage.toString() + "\n\n");
            System.out.println("======================================================================================");

        
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPayApproved_then_CREATE_2 (@Payload PayApproved payApproved) {
        
        //결재 호출이 동기호출이면 선결재되므로 데이터 미존재
        boolean isSync = true;
        if(isSync) {
            return;
        }
        
        try {

            if (!payApproved.validate()) return;

            System.out.println("======================================================================================");
            System.out.println("\n\n##### listener whenPayApproved_then_CREATE_2 : " + payApproved.toJson() + "\n\n");
            System.out.println("======================================================================================");
                        

            List<Mypage> mypageList = mypageRepository.findByOrderId(payApproved.getOrderId());
            for(Mypage mypage : mypageList){
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                
                mypage.setPayAmt(payApproved.getPayAmt());
                mypage.setStatus(payApproved.getStatus());

                mypageRepository.save(mypage);
            }
        
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenDeliveryStarted_then_CREATE_3 (@Payload DeliveryStarted deliveryStarted) {
        try {


            if (!deliveryStarted.validate()) return;

            System.out.println("\n\n##### listener whenDeliveryStarted_then_CREATE_3 : " + deliveryStarted.toJson() + "\n\n");

            List<Mypage> mypageList = mypageRepository.findByOrderId(deliveryStarted.getOrderId());
            for(Mypage mypage : mypageList){
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                
                if(StringUtils.isEmpty(deliveryStarted.getStatus())) {
                    deliveryStarted.setStatus("배송중");
                }
                mypage.setStatus(deliveryStarted.getStatus());

                mypageRepository.save(mypage);
            }
        
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderCanceled_then_UPDATE_1(@Payload OrderCanceled orderCanceled) {
        try {

            if (!orderCanceled.validate()) return;

            System.out.println("\n\n##### listener whenOrderCanceled_then_UPDATE_1 : " + orderCanceled.toJson() + "\n\n");
            
            List<Mypage> mypageList = mypageRepository.findByOrderId(orderCanceled.getId());
            for(Mypage mypage : mypageList){
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                
                if(StringUtils.isEmpty(orderCanceled.getStatus())) {
                    orderCanceled.setStatus("주문취소");
                }
                 mypage.setStatus(orderCanceled.getStatus());

                mypageRepository.save(mypage);
            }

            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPayCanceled_then_UPDATE_2(@Payload PayCanceled payCanceled) {

       
        try {
            if (!payCanceled.validate()) return;

            System.out.println("\n\n##### listener whenPayCanceled_then_UPDATE_2 : " + payCanceled.toJson() + "\n\n");

            List<Mypage> mypageList = mypageRepository.findByOrderId(payCanceled.getOrderId());
            for(Mypage mypage : mypageList){
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                mypage.setStatus(payCanceled.getStatus());


                // view 레파지 토리에 save
                mypageRepository.save(mypage);
            }
            


        }catch (Exception e){
            e.printStackTrace();
        }
    }

}