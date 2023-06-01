package ibf2022.batch3.assessment.csf.orderbackend.services;

import java.io.StringReader;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import ibf2022.batch3.assessment.csf.orderbackend.models.PizzaOrder;
import ibf2022.batch3.assessment.csf.orderbackend.respositories.OrdersRepository;
import ibf2022.batch3.assessment.csf.orderbackend.respositories.PendingOrdersRepository;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

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
        map.add("toppings", "chicken");
        map.add("comments", order.getComments());

        String url = UriComponentsBuilder.fromUriString("https://pizza-pricing-production.up.railway.app/order").toUriString();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = restTemplate.postForEntity(url, map, String.class);

        String payload = resp.getBody();
        String[] data = payload.split(",");
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
