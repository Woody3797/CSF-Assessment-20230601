package ibf2022.batch3.assessment.csf.orderbackend.controllers;

import java.io.StringReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ibf2022.batch3.assessment.csf.orderbackend.models.PizzaOrder;
import ibf2022.batch3.assessment.csf.orderbackend.services.OrderException;
import ibf2022.batch3.assessment.csf.orderbackend.services.OrderingService;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Controller
@RequestMapping
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderingService orderingService;

	// TODO: Task 3 - POST /api/order
    @PostMapping(path = "/api/order")
    @ResponseBody
    public ResponseEntity<String> placeOrder(@RequestBody String payload) throws OrderException {
        JsonReader jr = Json.createReader(new StringReader(payload));
        JsonObject data = jr.readObject();
        PizzaOrder order = new PizzaOrder();
        order.setName(data.getString("name"));
        order.setEmail(data.getString("email"));
        order.setSauce(data.getString("sauce"));
        order.setSize(data.getInt("size"));
        order.setComments(data.getString("comments"));
        // order.setTopplings(data.get("toppings"));
        order.setThickCrust(data.getString("base") == "thick" ? true : false);
        try {
            order = orderingService.placeOrder(order);
            return ResponseEntity.ok().body(toJson(order).toString());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Json.createObjectBuilder().add("error", e.getMessage()).build().toString());
        }
    }


	// TODO: Task 6 - GET /api/orders/<email>
    @GetMapping(path = "/api/orders/{email}")
    public ResponseEntity<String> getOrders(@PathVariable String email) {
        List<PizzaOrder> orders = orderingService.getPendingOrdersByEmail(email);
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (PizzaOrder o : orders) {
            JsonObject jo = toJson(o);
            jab.add(jo);
        }

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(jab.build().toString());
    }

	// TODO: Task 7 - DELETE /api/order/<orderId>
    @DeleteMapping(path = "/api/order/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable String orderId) {
        boolean result = orderingService.markOrderDelivered(orderId);
        if (!result) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().body("{}");
        }
    }




    private JsonObject toJson(PizzaOrder order) {
        return Json.createObjectBuilder()
        .add("orderId", order.getOrderId())
        .add("date", order.getDate().toString())
        .add("name", order.getName())
        .add("email", order.getEmail())
        .add("total", order.getTotal())
        .build();
    }
    
}
