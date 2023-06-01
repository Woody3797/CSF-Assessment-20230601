import { Component, OnInit, inject } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PizzaService } from '../pizza.service';

const SIZES: string[] = [
    "Personal - 6 inches",
    "Regular - 9 inches",
    "Large - 12 inches",
    "Extra Large - 15 inches"
]

const PIZZA_TOPPINGS: string[] = [
    'chicken', 'seafood', 'beef', 'vegetables',
    'cheese', 'arugula', 'pineapple'
]

@Component({
    selector: 'app-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

    pizzaSize = SIZES[2]

    constructor() { }
    utility = inject(PizzaService)
    fb = inject(FormBuilder)
    router = inject(Router)

    form!: FormGroup
    toppings!: FormGroup

    ngOnInit(): void {
        this.toppings = this.fb.group({
            0: this.fb.control(false),
            1: this.fb.control(false),
            2: this.fb.control(false),
            3: this.fb.control(false),
            4: this.fb.control(false),
            5: this.fb.control(false),
            6: this.fb.control(false),
        })

        this.form = this.fb.group({
            name: this.fb.control('', [Validators.required]),
            email: this.fb.control('', [Validators.required]),
            size: this.fb.control(2, [Validators.required]),
            base: this.fb.control('thin', [Validators.required]),
            sauce: this.fb.control('signature', [Validators.required]),
            toppings: this.toppings,
            comments: this.fb.control('',),
        })
    }

    updateSize(size: string) {
        this.pizzaSize = SIZES[parseInt(size)]
    }

    placeOrder() {
        // var checkedToppings!: string[]
        
        this.utility.placeOrder(this.form).subscribe({
            next: () => this.router.navigate(['/orders', this.form.value['email']]),
            error: e => alert(e.message)
        })
        
    }

    invalid() {
        return this.form.invalid
    }

}
