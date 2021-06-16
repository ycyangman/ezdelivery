package ezdelivery;

import ezdelivery.config.kafka.KafkaProcessor;

import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class PolicyHandler{
    @Autowired PaymentRepository paymentRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCanceled_CancelPay(@Payload OrderCanceled orderCanceled){

        if(!orderCanceled.validate()) return;

        System.out.println("\n\n##### listener CancelPay : " + orderCanceled.toJson() + "\n\n");

        long orderId = orderCanceled.getId()== null ? 0 : orderCanceled.getId();

 		List<Payment> payments = paymentRepository.findByOrderId(orderId);

		if(ObjectUtils.isEmpty(payments)) {

			System.out.println("\n\n##### payments table no data found by : " + orderId + "\n\n");
			return ;

		}

		Payment payment =  payments.get(0);
        payment.setStatus("결재취소");
        paymentRepository.save(payment);

           
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
