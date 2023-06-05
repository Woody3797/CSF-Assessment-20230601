package ibf2022.batch3.assessment.csf.orderbackend.respositories;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import ibf2022.batch3.assessment.csf.orderbackend.models.PizzaOrder;

@Repository
public class OrdersRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

	// TODO: Task 3
	// WARNING: Do not change the method's signature.
	// Write the native MongoDB query in the comment below
	// db.orders.insertOne({
    //     _id: "orderId",
    //     date: "date",
    //     total: "total",
    //     name: "name",
    //     email: "email",
    //     sauce: "sauce",
    //     size: "size",
    //     comments: "comments",
    //     toppings: ["topping0", "topping1"],
    //     crust: "thick"
    // })
	public void add(PizzaOrder order) {
        Document doc = toDocument(order);
        mongoTemplate.insert(doc, "orders");
	}
	
	// TODO: Task 6
	// WARNING: Do not change the method's signature.
	// Write the native MongoDB query in the comment below
	// db.orders.find({
    //     email: 'b@a.com',
    //     delivered: {$ne: true}
    // }).sort({
    //     date: -1
    // });
	public List<PizzaOrder> getPendingOrdersByEmail(String email) {
        Query query = Query.query(Criteria.where("email").is(email));
        query.addCriteria(Criteria.where("delivered").ne(true)).with(Sort.by(Direction.DESC, "date"));

        List<Document> docs = mongoTemplate.find(query, Document.class, "orders");
        List<PizzaOrder> orders = new ArrayList<>();
        for (Document d : docs) {
            PizzaOrder order = convertFromDoc(d);
            orders.add(order);
        }
        
		return orders;
	}

	// TODO: Task 7
	// WARNING: Do not change the method's signature.
	// Write the native MongoDB query in the comment below
	// db.orders.findOneAndUpdate(
    //     { "_id": 'id' },
    //     { $set: {"delivered" : true}}
    // )
	public boolean markOrderDelivered(String orderId) {
        Query query = Query.query(Criteria.where("_id").is(orderId));
        Update update = new Update().set("delivered", true);
        UpdateResult res = mongoTemplate.upsert(query, update, Document.class, "orders");
		return res.getModifiedCount() > 0;
	}


    private Document toDocument(PizzaOrder order) {
        Document doc = new Document();
        doc.put("_id", order.getOrderId());
        doc.put("date", order.getDate());
        doc.put("total", order.getTotal());
        doc.put("name", order.getName());
        doc.put("email", order.getEmail());
        doc.put("sauce", order.getSauce());
        doc.put("size", order.getSize());
        doc.put("comments", order.getComments());
        doc.put("toppings", order.getToppings());
        doc.put("crust", order.getThickCrust() == true ? "thick" : "thin");
        return doc;
    }

    private PizzaOrder convertFromDoc(Document d) {
        PizzaOrder order = new PizzaOrder();
        order.setOrderId(d.getString("_id"));
        order.setDate(d.getDate("date"));
        order.setName(d.getString("name"));
        order.setEmail(d.getString("email"));
        order.setTotal(d.getDouble("total").floatValue());
        return order;
    }
}
