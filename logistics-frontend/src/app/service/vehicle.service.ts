import {Injectable} from "@angular/core";
import {Http, RequestOptions, Headers, Response} from '@angular/http';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';
import {VehiclesData} from "../model/vehicles.data";
import {VehicleParams} from "../model/vehicle.params";
import { environment } from '../../environments/environment';

@Injectable()
export class VehicleService {
  headers: Headers;
  requestOptions: RequestOptions;
  backendUrl: string;

  constructor (private http: Http) {
    this.headers = new Headers({'Content-Type': 'application/json'});
    this.requestOptions = new RequestOptions({headers: this.headers});
    this.backendUrl = environment.backendUrl;
  }

  getVehiclesData() : Observable<VehiclesData> {
    return this.http.get(this.backendUrl +'/api/vehicles/')
      .map(this.extractData)
      .catch(this.handleError);
  }

  getVehiclesPath(name: string) {
    return this.http.get(this.backendUrl +'/api/vehicles/' + name + '/path')
      .map(this.extractData)
      .catch(this.handleError);
  }

  addVehicle(vehicleParams: VehicleParams) {
    return this.http.put(
      this.backendUrl +'/api/vehicles/add',
      JSON.stringify(vehicleParams),
      this.requestOptions
    );
  }

  deleteVehicle(vehicleName: string) {
    return this.http.delete(
      this.backendUrl +'/api/vehicles/' + vehicleName + '/delete',
      this.requestOptions
    ).catch(this.handleError);
  }

  driveTo(vehicleParams: VehicleParams) {
    return this.http.post(
      this.backendUrl +'/api/vehicles/drive',
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
