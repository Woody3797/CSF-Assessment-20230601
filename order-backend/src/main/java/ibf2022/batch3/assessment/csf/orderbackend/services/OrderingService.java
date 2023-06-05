package ibf2022.batch3.assessment.csf.orderbackend.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import ibf2022.batch3.assessment.csf.orderbackend.models.PizzaOrder;
import ibf2022.batch3.assessment.csf.orderbackend.respositories.OrdersRepository;
import ibf2022.batch3.assessment.csf.orderbackend.respositories.PendingOrdersRepository;

@Service
public class OrderingService {

	@Autowired
	private OrdersRepository ordersRepo;

	@Autowired
	private PendingOrdersRepository pendingOrdersRepo;
	
	// TODO: Task 5
	// WARNING: DO NOT CHANGE THE METHOD'S SIGNATURE
	public PizzaOrder placeOrder(PizzaOrder order) throws OrderException {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", order.getName());
        map.add("email", order.getEmail());
        map.add("sauce", order.getSauce());
        map.add("size", order.getSize().toString());
        map.add("thickCrust", order.getThickCrust().toString());
        String regex = "[\\[\\]]";
        map.add("toppings", order.getToppings().toString().replaceAll(regex, ""));
        map.add("comments", order.getComments());

        String url = "https://defiant-veil-production.up.railway.app/api/getorderprice";
        RestTemplate restTemplate = new RestTemplate();
        
        // RequestEntity<MultiValueMap<String, String>> req = RequestEntity.post(url).contentType(MediaType.APPLICATION_FORM_URLENCODED).body(map);
		// ResponseEntity<String> resp = restTemplate.exchange(req, String.class);
        
        ResponseEntity<String> resp = restTemplate.postForEntity(url, map, String.class);

        String payload = resp.getBody();
        System.out.println(payload);
        @SuppressWarnings("null")
        String[] data = payload.replaceAll("[^0-9a-zA-Z,.]+", "").split(",");
        order.setOrderId(data[0]);
        order.setDate(new Date(Long.parseLong(data[1])));
        order.setTotal(Float.parseFloat(data[2]));
        ordersRepo.add(order);
        pendingOrdersRepo.add(order);
		return order;
	}

	// For Task 6
	// WARNING: Do not change the method's signature or its implemenation
	public List<PizzaOrder> getPendingOrdersByEmail(String email) {
		return ordersRepo.getPendingOrdersByEmail(email);
	}

	// For Task 7
	// WARNING: Do not change the method's signature or its implemenation
	public boolean markOrderDelivered(String orderId) {
		return ordersRepo.markOrderDelivered(orderId) && pendingOrdersRepo.delete(orderId);
	}


}
