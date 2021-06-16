package ezdelivery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

 @RestController
 public class ReviewController {

	@Autowired
	ReviewRepository reviewRepository;

	@Autowired
	MypageRepository mypageRepository;

    @RequestMapping(value = "/reviews/{storeId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getReviews(@PathVariable("storeId") String inStoreId) {
        
		System.out.println("\n\n##### getReviews storeId : " + inStoreId + "\n\n");
		long storeId = Long.parseLong(inStoreId);
        List<Review> reviews = reviewRepository.findByStoreId(storeId);
		
		Map<String, Object> result = new HashMap<>();
		result.put("data", reviews);
        
		return ResponseEntity.ok().body(result);
    }

	@RequestMapping(value = "/mypages/mypages.do", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> getMypages(@RequestBody HashMap<String, String> userMap) {
        
		System.out.println("\n\n##### getMypages userMap : " + userMap + "\n\n");
		
		String  type = userMap.get("type"); // 1: 호스트, 2:게스트
		String  myName = userMap.get("myName");

		Map<String, Object> result = new HashMap<>();

		List<Mypage> mypages = new ArrayList<>();

		if(myName==null || "".equals(myName)) {
			Iterable<Mypage> mypageIt = mypageRepository.findAll();

			if( mypageIt!= null)
			{
				mypageIt.forEach(mypages::add);
			}
		}
		else {
			if("1".equals(type)) {
				mypages = mypageRepository.findByHost(myName);
			}
			else {
				mypages = mypageRepository.findByGuestName(myName);
			}

			
		}

		result.put("data", mypages);
        
		return ResponseEntity.ok().body(result);
    }
	
 }
