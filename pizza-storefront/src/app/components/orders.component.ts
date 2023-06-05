import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { Observable, Subscription, firstValueFrom, map } from 'rxjs';
import { Order } from '../model';
import { PizzaService } from '../pizza.service';
import { ActivatedRoute, Router } from '@angular/router';
import { formatDate } from '@angular/common';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent implements OnInit, OnDestroy {

    utility = inject(PizzaService)
    activatedRoute = inject(ActivatedRoute)
    router = inject(Router)

    orders$!: Observable<Order[] | undefined>
    delivered$!: Subscription
    email = ''

    ngOnInit(): void {
        this.utility.email = this.activatedRoute.snapshot.params['email']
        this.orders$ = this.utility.getOrders(this.utility.email).pipe(map(result => {
            result.forEach((order: Order) => {
                let date = order.date.replace('SGT', 'GMT+0800')
                order.date = formatDate(new Date(Date.parse(date)), 'E, dd MMM yyyy || h:mm:ss aa z', 'en_US')
            })
            return result
        }))
        this.email = this.utility.email
    }

    process(orderId: string) {
        this.delivered$ = this.utility.delivered(orderId).subscribe(() => {
            this.orders$ = this.utility.getOrders(this.utility.email).pipe(map(result => {
                if (result?.length == 0) {
                    this.orders$ = new Observable<undefined>
                }
                result.forEach((order: Order) => {
                    let date = order.date.replace('SGT', 'GMT+0800')
                    order.date = formatDate(new Date(Date.parse(date)), 'E, dd MMM yyyy || h:mm:ss aa z', 'en_US')
                })
                return result
            }))
        })
        
        // Using Observable and subscribe to get values
        // this.delivered$ = this.utility.delivered(orderId).subscribe(() => {
        //     this.utility.getOrders(this.utility.email).subscribe(res => {
        //         this.orders = res
        //     })
        // })

        // Using Observable and async to get values
        // this.delivered$ = this.utility.delivered(orderId).subscribe()
        // this.orders$ = this.utility.getOrders(this.utility.email).pipe(
        //     debounceTime(100),
        //     map(res => {
        //         if (res.length == 0) {
        //             return undefined
        //         }
        //         this.orders = res
        //         return res
        //     })
        // )

        // Using Promise to get values
        // this.utility.delivered(orderId).then(() => {
        //     this.utility.getOrders(this.email).subscribe(res => {
        //         if (res.length == 0) {
        //             this.orders$ = new Observable<undefined>
        //         }
        //         this.orders = res
        //     })
        // })
    }

    ngOnDestroy(): void {
        if (this.delivered$) {
            this.delivered$.unsubscribe()
        }
    }
}
