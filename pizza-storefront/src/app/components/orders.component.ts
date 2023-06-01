import { Component, OnInit, inject } from '@angular/core';
import { Observable, Subscription, map } from 'rxjs';
import { Order } from '../model';
import { PizzaService } from '../pizza.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent implements OnInit {

    utility = inject(PizzaService)
    activatedRoute = inject(ActivatedRoute)


    orders$!: Observable<Order[]>
    ordersSub$!: Subscription
    email = ''

    ngOnInit(): void {
        this.utility.email = this.activatedRoute.snapshot.params['email']
        this.orders$ = this.utility.getOrders(this.utility.email).pipe(map(result => {
            return result
        }))
        this.email = this.utility.email
    }


    process(orderId: string) {
        this.utility.delivered(orderId).subscribe()
    }
}
