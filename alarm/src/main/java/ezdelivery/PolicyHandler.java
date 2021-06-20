package ezdelivery;

import ezdelivery.config.kafka.KafkaProcessor;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PolicyHandler{
    @Autowired MsgRepository msgRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_SendMsg(@Payload Ordered ordered){

        if(!ordered.validate()) return;

        System.out.println("\n\n##### wheneverOrdered_SendMsg : " + ordered.toJson() + "\n\n");


        String msg =  "주문접수:"+ordered.getId() +", 상세내역:" + ordered.toString();

        if(!StringUtils.isEmpty(ordered.getHost())) {
            sendMsg(ordered.getHost(), msg);
        }
        
        if(!StringUtils.isEmpty(ordered.getGuestName())) {
            sendMsg(ordered.getGuestName(), msg);
        }
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCanceled_SendMsg(@Payload OrderCanceled orderCanceled){

        if(!orderCanceled.validate()) return;

        System.out.println("\n\n##### wheneverOrderCanceled_SendMsg : " + orderCanceled.toJson() + "\n\n");

        String msg =  "주문취소:"+orderCanceled.getId() +", 상세내역:" + orderCanceled.toString() ;

        if(!StringUtils.isEmpty(orderCanceled.getHost())) {
            sendMsg(orderCanceled.getHost(), msg);
        }
        
        if(!StringUtils.isEmpty(orderCanceled.getGuestName())) {
            sendMsg(orderCanceled.getGuestName(), msg);
        }
            
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPayApproved_SendMsg(@Payload PayApproved payApproved){

        if(!payApproved.validate()) return;

        System.out.println("\n\n##### wheneverPayApproved_SendMsg : " + payApproved.toJson() + "\n\n");

        String msg =  "주문결재:"+payApproved.getOrderId() + ", 상세내역:"  + payApproved.toString();

        if(!StringUtils.isEmpty(payApproved.getHost())) {
            sendMsg(payApproved.getHost(), msg);
        }
       
        if(!StringUtils.isEmpty(payApproved.getGuestName())) {
            sendMsg(payApproved.getGuestName(), msg);
        }
            
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPayCanceled_SendMsg(@Payload PayCanceled payCanceled){

        if(!payCanceled.validate()) return;

        System.out.println("\n\n##### wheneverPayCanceled_SendMsg : " + payCanceled.toJson() + "\n\n");

        String msg =  "결재취소:"+payCanceled.getOrderId() + ", 상세내역:"  + payCanceled.toString();

        if(!StringUtils.isEmpty(payCanceled.getGuestName())) {
            sendMsg(payCanceled.getGuestName(), msg);
        }
            
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDeliveryStarted_SendMsg(@Payload DeliveryStarted deliveryStarted){

        if(!deliveryStarted.validate()) return;

        System.out.println("\n\n##### wheneverDeliveryStarted_SendMsg : " + deliveryStarted.toJson() + "\n\n");

        String msg =  "orderId:"+deliveryStarted.getOrderId() + "_" + deliveryStarted.getStatus();
       
        /*
        if(!StringUtils.isEmpty(deliveryStarted.getGuestName())) {
            sendMsg(deliveryStarted.getGuestName(), msg);
        }
        */
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


    private void sendMsg(String receiver, String message) {
        Msg msg = new Msg();
        msg.setReceiver(receiver);
        msg.setMessage(message);

        SimpleDateFormat defaultSimpleDateFormat = new SimpleDateFormat("YYYY.MM.dd HH:mm:ss");
        msg.setSendDateTime(defaultSimpleDateFormat.format(new Date()));

        msgRepository.save(msg);
    }

}
