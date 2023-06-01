import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { Order } from "./model";

const URL = 'http://localhost:8080'

@Injectable()
export class PizzaService {

    http = inject(HttpClient)

    email = ''
    orderId = ''

    // TODO: Task 3
    // You may add any parameters and return any type from placeOrder() method
    // Do not change the method name
    placeOrder(data: any): Observable<Order> {
        const formData = data.getRawValue()
        let jsonData = JSON.stringify(formData)

        return this.http.post<Order>(URL + '/api/order', jsonData)
    }

    // TODO: Task 5
    // You may add any parameters and return any type from getOrders() method
    // Do not change the method name
    getOrders(email: any): Observable<Order[]> {

        return this.http.get<Order[]>(URL + '/api/orders/' + email)
    }

    // TODO: Task 7
    // You may add any parameters and return any type from delivered() method
    // Do not change the method name
    delivered(orderId: string) {

        return this.http.delete(URL + '/api/order/' + orderId)
    }

}
