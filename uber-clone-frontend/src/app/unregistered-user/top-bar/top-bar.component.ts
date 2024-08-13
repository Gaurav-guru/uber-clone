import { Component } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css','../../app.component.css']
})
export class TopBarComponent {

  constructor(private router : Router) { }

  redirectToLoginRegister() {
    this.router.navigate(['/entrance']).then();
  }

}
