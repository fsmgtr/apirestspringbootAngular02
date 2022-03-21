import { Component } from '@angular/core';
import { LoginService } from './services/login.service';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'frontEndSpringAngular';

  usuario = {login: '', senha: ''};
constructor(private loginService: LoginService ){

}

public login(){
  this.loginService.login(this.usuario);
}

}
