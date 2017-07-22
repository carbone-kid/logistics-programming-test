import {Component, Injectable} from '@angular/core';
import {VehicleService} from "./service/vehicle.service";
import {VehicleParams} from "./model/vehicle.params";
import {AgmCoreModule, MouseEvent, AgmPolyline, AgmPolylinePoint} from '@agm/core';
import {Observable} from "rxjs/Observable";
import {Subscription} from "rxjs/Subscription";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [VehicleService]
})

@Injectable()
export class AppComponent {
  errorMessage: string;

  carSelected: boolean;
  selectedLat: number;
  selectedLng: number;
  selectedCarPath;

  newCar: string;
  lat: number = 52.01033382173769;
  lng: number = 4.349137544631958;
  selectedCarName: string;
  vehicles: VehicleParams[] = [];

  vehiclesObserver = Observable.interval(1000 * 3);
  pathObserver = Observable.interval(1000 * 3);
  pathObserverSubscription: Subscription;

  constructor(private vehiclesService: VehicleService) {
    this.carSelected = false;

    this.vehiclesObserver.subscribe(x => {
      this.vehiclesService.getVehiclesData()
        .subscribe(
          vehiclesData => this.vehicles = vehiclesData.vehicles,
          error => this.errorMessage = <any>error
        );
    });
  }

  onSelectCurrentCar(vehicleParams: VehicleParams) {
    if(this.pathObserverSubscription != null) {
      this.pathObserverSubscription.unsubscribe();
    }

    this.pathObserverSubscription = this.pathObserver.subscribe(x => {
      this.vehiclesService.getVehiclesPath(vehicleParams.name)
        .subscribe(
          path => this.selectedCarPath = path
        );
    });

    this.lat = vehicleParams.lat;
    this.lng = vehicleParams.lng;
    this.selectedCarName = vehicleParams.name;
    this.carSelected = true;
  }

  onSelectCoordinates(event: MouseEvent) {
    this.selectedLat = event.coords.lat;
    this.selectedLng = event.coords.lng;
  }

  addVehicle(name: string, lat: number, lng: number) {
    this.vehiclesService.addVehicle({name: name, lat: this.selectedLat, lng: this.selectedLng}).subscribe(
      ignore => {},
      error => this.errorMessage = <any>error
    );
  }

  deleteVehicle(name: string) {
    this.vehiclesService.deleteVehicle(name).subscribe(
      ignore => {
        if(this.pathObserverSubscription != null) {
          this.pathObserverSubscription.unsubscribe();
        }
        this.selectedCarPath = null;
        this.carSelected = false;
        this.selectedCarName = null;
      },
      error => this.errorMessage = <any>error
    );
  }

  driveTo(name: string, lat: number, lng: number) {
    this.vehiclesService.driveTo({name: name, lat: lat, lng: lng}).subscribe(
      ignore => {},
      error => this.errorMessage = <any>error
    );
  }
}
