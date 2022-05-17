package com.satellite.position.cache;

import com.satellite.position.domain.Satellite;

import java.util.List;

public class SatellitesSingleton {

    List<Satellite> satelliteList;
    private static SatellitesSingleton singleton;
    public  static SatellitesSingleton getSatellitesSingleton() {
        if (singleton==null) {
            singleton=new SatellitesSingleton();
        }
        return singleton;
    }

    public List<Satellite> getSatelliteList() {
        return satelliteList;
    }
    public List<Satellite> setSatelliteList(List<Satellite> satelliteList) {
        return this.satelliteList = satelliteList;
    }
}
