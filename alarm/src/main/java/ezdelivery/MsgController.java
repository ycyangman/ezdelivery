package ezdelivery;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 @RestController
 public class MsgController {


    @Autowired
    MsgRepository msgRepository;

    @RequestMapping(value = "/alarms.do", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getMyalarms(@RequestBody HashMap<String, String> userMap) {
        
		System.out.println("\n\n##### getMypages userMap : " + userMap + "\n\n");
		
		String  myName = userMap.get("myName");

		Map<String, Object> result = new HashMap<>();

		List<Msg> msgs = new ArrayList<>();

		if(myName==null || "".equals(myName)) {
			Iterable<Msg> msgIt = msgRepository.findAll();

			if( msgIt!= null)
			{
				msgIt.forEach(msgs::add);
			}
		}
		else {
			msgs = msgRepository.findByReceiver(myName);
			
		}

		result.put("data", msgs);
        
		return ResponseEntity.ok().body(result);
    }
	
    
 }
