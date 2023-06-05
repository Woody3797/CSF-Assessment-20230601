package ibf2022.batch3.assessment.csf.orderbackend.controllers;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class PizzaPriceRestController {
    
    @SuppressWarnings("null")
    @PostMapping(path = "/api/getorderprice", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> returnOrderPricing(@RequestBody MultiValueMap<String, String> map) {
        double price = 10.0;
        try {
            String name = map.getFirst("name");
            String email = map.getFirst("email");
            String sauce = map.getFirst("sauce");
            String size = map.getFirst("size");
            String thickCrust = map.getFirst("thickCrust");
            String toppingsArr = map.getFirst("toppings");

            if (!StringUtils.hasText(name) || !StringUtils.hasText(email) || !StringUtils.hasText(sauce) || !StringUtils.hasText(size) || !StringUtils.hasText(thickCrust) || !StringUtils.hasText(toppingsArr) || !toppingsArr.matches("[,a-z\s]*")) {
                throw new Exception();
            }

            switch(sauce) {
                case "signature":
                    price += 7;
                    break;
                case "salsa":
                    price += 6;
                    break;
                case "smoky":
                    price += 8;
                    break;
                case "napolitana":
                    price += 10;
                    break;
                default:
                    price += 5;
            }

            switch (size) {
                case "1":
                    price *= 1.3;
                    break;
                case "2":
                    price *= 1.5;
                    break;
                case "3":
                    price *= 2;
                    break;
                default:
                    price *= 1;
            }

            if (thickCrust.contains("true")) {
                price += 3;
            } else if (thickCrust.contains("false")) {
                price += 1;
            }

            String toppings = toppingsArr.trim();
            String[] premToppings = {"beef", "seafood", "arugula"};
            String[] regToppings = {"chicken", "vegetables", "cheese", "pineapple"};

            for (String t : premToppings) {
                if (toppings.contains(t)) {
                    price += 2.5;
                }
            }
            for (String t : regToppings) {
                if (toppings.contains(t)) {
                    price += 1.5;
                }
            }
            
            String id = UUID.randomUUID().toString().substring(0, 8);
            String result = id + ", " + System.currentTimeMillis() + ", " + String.format("%.2f", price);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            
            return new ResponseEntity<>(result, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid request");
        }
    }
}
