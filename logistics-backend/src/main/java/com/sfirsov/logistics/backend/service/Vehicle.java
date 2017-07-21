package com.sfirsov.logistics.backend.service;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.sfirsov.logistics.backend.model.VehicleParams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Vehicle {
    private String name;
    private final GeoApiContext context;
    private LatLng currentPosition;
    private List<LatLng> path;

    public Vehicle(GeoApiContext context, VehicleParams params) {
        this.context = context;
        this.name = params.getName();
        this.currentPosition = new LatLng(params.getLat(), params.getLng());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public synchronized LatLng getCurrentPosition() {
        return currentPosition;
    }

    public synchronized void setCurrentPosition(LatLng currentPosition) {
        this.currentPosition = currentPosition;
    }

    public List<LatLng> getPath() {
        return path;
    }

    public Future<LatLng> driveTo(LatLng destination, ExecutorService executorService) {
        return executorService.submit(() -> {
            DirectionsResult result = DirectionsApi.newRequest(context).origin(currentPosition).destination(destination).await();

            path = result.routes[0].overviewPolyline.decodePath();

            for(int i=0; i<path.size()-1; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                LatLng startPoint = path.get(i);
                LatLng endPoint = path.get(i+1);

                double distance = getDistanceFromLatLonInKm(path.get(i), path.get(i+1));
                LocalDateTime startTime = LocalDateTime.now();
                boolean onTheSamePart = true;

                while (onTheSamePart && !Thread.currentThread().isInterrupted()) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }

                    LatLng currentPosition = interpolate(startPoint, startTime, endPoint, 20, distance, LocalDateTime.now());
                    setCurrentPosition(currentPosition);

                    if(Math.abs(currentPosition.lat - endPoint.lat) < 0.0001 && Math.abs(currentPosition.lng - endPoint.lng) < 0.0001) {
                        onTheSamePart = false;
                    }
                }
            }

            return getCurrentPosition();
        });
    }

    private double getDistanceFromLatLonInKm(LatLng from, LatLng to) {
        double earthRadius = 6371;
        double distanceLat = Math.toRadians(to.lat - from.lat);
        double distanceLng = Math.toRadians(to.lng - from.lng);
        double a = Math.sin(distanceLat/2) * Math.sin(distanceLat/2) +
                        Math.cos(Math.toRadians(from.lat)) * Math.cos(Math.toRadians(to.lat)) *
                                Math.sin(distanceLng/2) * Math.sin(distanceLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;
        return distance;
    }

    private double hoursToMillis(double hours) {
        return hours * 60 * 60 * 1000;
    }

    private LatLng interpolate(LatLng startPoint, LocalDateTime startTime, LatLng endPoint, double speed, double distance, LocalDateTime currentTime) {
        double timeToCoverDistance = hoursToMillis(distance / speed);
        double elapsedTime = java.time.Duration.between(startTime, currentTime).toMillis();
        double coveredPart = elapsedTime / timeToCoverDistance;
        double currentLat = startPoint.lat + ((endPoint.lat - startPoint.lat) * coveredPart);
        double currentLng = startPoint.lng + ((endPoint.lng - startPoint.lng) * coveredPart);
        return new LatLng(currentLat, currentLng);
    }
}
