import { HttpClient } from '@angular/common/http';
import { error } from '@angular/compiler/src/util';
import { Injectable } from '@angular/core';
import { AppConstants } from '../app-constants';

@Injectable({
  providedIn: 'root'
})
export class LoginService{

  constructor(private http: HttpClient) { }

  login(usuario: { login: any; senha?: any; }){
return this.http.post(AppConstants.baseLogin, JSON.stringify(usuario)).subscribe(data => {

  //retorno http
var token = JSON.parse(JSON.stringify(data)).Authorization.split(' ')[1];
localStorage.setItem("token" , token);
}, error =>{
console.error("Erro ao fazer login");
alert("ACESSO NEGADO!");
}
);
}


}
