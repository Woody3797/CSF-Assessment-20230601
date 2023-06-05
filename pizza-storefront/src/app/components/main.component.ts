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

    pizzaSize = SIZES[0]

    constructor() { }
    utility = inject(PizzaService)
    fb = inject(FormBuilder)
    router = inject(Router)

    form!: FormGroup
    toppings!: FormGroup
    randomName = Math.random().toString(36).slice(-5).replace(/\d/g, String.fromCharCode(Math.random()*26+97))

    ngOnInit(): void {
        this.toppings = this.fb.group({
            chicken: this.fb.control(false),
            seafood: this.fb.control(false),
            beef: this.fb.control(false),
            vegetables: this.fb.control(false),
            cheese: this.fb.control(false),
            arugula: this.fb.control(false),
            pineapple: this.fb.control(false),
        })

        this.form = this.fb.group({
            name: this.fb.control(this.randomName, [Validators.required]),
            email: this.fb.control('b@a.com', [Validators.required, Validators.email]),
            size: this.fb.control(0, [Validators.required]),
            base: this.fb.control('thin', [Validators.required]),
            sauce: this.fb.control('classic', [Validators.required]),
            toppings: this.toppings,
            comments: this.fb.control('',),
        })
    }

    updateSize(size: string) {
        this.pizzaSize = SIZES[parseInt(size)]
    }

    placeOrder() {        
        this.form.value.toppings = this.checkToppings()

        this.utility.placeOrder(this.form.value).subscribe({
            next: () => this.router.navigate(['/orders', this.form.value['email']]),
            error: e => {
                alert(e.message)
                // location.reload()
            }
        })
    }

    invalid(): boolean {
        return (this.checkToppings().length == 0 || this.form.invalid)
        // if (this.checkToppings().length == 0 || this.form.invalid) {
        //     return true
        // }
        // return false
    }

    checkToppings(): string[] {
        let selectedToppings: string[] = [];
        Object.keys(this.toppings.controls).forEach(controlName => {
            let controlValue = this.toppings.get(controlName)?.value;
            if (controlValue === true) {
                selectedToppings.push(controlName);
            }
        });
        return selectedToppings;
        // let checkedToppings: string[] = []
        // let top = this.form.value.toppings
        // for (const key in top) {
        //     if (top[key] == true) {
        //         checkedToppings.push(key)
        //     }
        // }
        // return checkedToppings
    }
}
