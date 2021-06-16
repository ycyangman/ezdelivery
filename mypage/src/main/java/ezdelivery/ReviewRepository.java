package ezdelivery;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource(collectionResourceRel="reviews", path="reviews")
public interface ReviewRepository extends PagingAndSortingRepository<Review, Long>{

	List<Review> findByStoreId(Long storeId);
	
}
