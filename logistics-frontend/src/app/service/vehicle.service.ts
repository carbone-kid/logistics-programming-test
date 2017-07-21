import {Injectable} from "@angular/core";
import {Http, RequestOptions, Headers, Response} from '@angular/http';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';
import {VehiclesData} from "../model/vehicles.data";
import {VehicleParams} from "../model/vehicle.params";

@Injectable()
export class VehicleService {
  headers: Headers;
  requestOptions: RequestOptions;

  constructor (private http: Http) {
    this.headers = new Headers({'Content-Type': 'application/json'});
    this.requestOptions = new RequestOptions({headers: this.headers});
  }

  getVehiclesData() : Observable<VehiclesData> {
    return this.http.get('http://localhost:8080/api/vehicles/')
      .map(this.extractData)
      .catch(this.handleError);
  }

  getVehiclesPath(name: string) {
    return this.http.get('http://localhost:8080/api/vehicles/' + name + '/path')
      .map(this.extractData)
      .catch(this.handleError);
  }

  addVehicle(vehicleParams: VehicleParams) {
    return this.http.put(
      'http://localhost:8080/api/vehicles/add',
      JSON.stringify(vehicleParams),
      this.requestOptions
    );
  }

  deleteVehicle(vehicleName: string) {
    return this.http.delete(
      'http://localhost:8080/api/vehicles/' + vehicleName + '/delete',
      this.requestOptions
    ).catch(this.handleError);
  }

  driveTo(vehicleParams: VehicleParams) {
    return this.http.post(
      'http://localhost:8080/api/vehicles/drive',
      JSON.stringify(vehicleParams),
      this.requestOptions
    ).catch(this.handleError);
  }

  private extractData(res: Response) {
    let body = res.json();
    return body || {};
  }

  private handleError (error: Response | any) {
    let errMsg: string;
    if (error instanceof Response) {
      const body = error.json() || '';
      const err = body.error || JSON.stringify(body);
      errMsg = error.toString() + "; " + err;
    } else {
      errMsg = error.message ? error.message : error.toString();
    }
    //console.error(errMsg);
    return Observable.throw(errMsg);
  }
}
