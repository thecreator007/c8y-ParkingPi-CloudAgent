package com.softwareag.parkingpi.agent;

import c8y.DistanceMeasurement;
import com.cumulocity.model.authentication.CumulocityCredentials;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectReferenceRepresentation;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.rest.representation.measurement.MeasurementRepresentation;
import com.cumulocity.sdk.client.Platform;
import com.cumulocity.sdk.client.PlatformImpl;
import com.cumulocity.sdk.client.measurement.MeasurementApi;
import com.cumulocity.sdk.client.measurement.MeasurementCollection;
import com.cumulocity.sdk.client.measurement.MeasurementFilter;

import java.math.BigDecimal;
import java.util.*;


public class ParkingPiCloudAgent {
    public static void main(String[] args) throws InterruptedException {


//hostname:https://developer.cumulocity.com

        Platform platform = new PlatformImpl("<hostname>", new CumulocityCredentials("<username>", "<password>"));
        GId parentID = new FoundDevice().discoverParentID(platform.getInventoryApi());
        ManagedObjectRepresentation mo = platform.getInventoryApi().get(parentID);
        List<ManagedObjectReferenceRepresentation> l = mo.getChildDevices().getReferences();
        GId[] childGid = new GId[l.size()];
        Map<String, MeasurementArray> valueMap = new HashMap<>();

        for (int i = 0; i < l.size(); i++) {
            childGid[i] = l.get(i).getManagedObject().getId();
        }
        MeasurementApi measurementApi = platform.getMeasurementApi();
        while (true) {

            for (GId aChildGid : childGid) {

                String mob = platform.getInventoryApi().get(aChildGid).getName();
                System.out.println(aChildGid.getValue() +"   "+ mob);
                ArrayList<MeasurementArray> distArray = new ArrayList<MeasurementArray>();
                MeasurementFilter mf = new MeasurementFilter();
                Calendar cal = Calendar.getInstance();
               /* cal.add(Calendar.HOUR,+5);
                cal.add(Calendar.MINUTE,+30);*/
                Date toDate = cal.getTime();
                cal.add(Calendar.SECOND, -40);
                Date fromDate = cal.getTime();
                mf.bySource(aChildGid);
                mf.byDate(fromDate, toDate);
                MeasurementCollection mc = measurementApi.getMeasurementsByFilter(mf);

                /*for (MeasurementRepresentation mor : mc.get().allPages()) {
                    BigDecimal adVal = mor.get(DistanceMeasurement.class).getDistance().getValue();
                    distArray.add(new MeasurementArray(adVal, mor.getDateTime()));
                }*/
                for (MeasurementRepresentation mor : mc.get()) {
                    BigDecimal adVal = mor.get(DistanceMeasurement.class).getDistance().getValue();
                    distArray.add(new MeasurementArray(adVal, mor.getDateTime()));
                }
                distArray.sort(new compareTime());
                if (distArray.size() > 0) {
                    valueMap.put(mob,distArray.get(0));
                }
                if (!valueMap.containsKey(mob) || valueMap.get(mob).getValue().doubleValue() > 80.00) {
                    System.out.println(mob + " is Empty with distance " + (!valueMap.containsKey(mob) ? 0 : valueMap.get(mob).getValue()) + " received at " + (!valueMap.containsKey(mob) ? "NO TIME" : valueMap.get(mob).getDateTime()));
                } else {
                    System.out.println(mob +" is Occupied with distance " + valueMap.get(mob).getValue() + " received at " + valueMap.get(mob).getDateTime());
                }
                }
            System.out.println("#$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$#");
        }
    }
}
class compareTime implements Comparator<MeasurementArray> {
    public int compare(MeasurementArray d1,MeasurementArray d2) {
        return d2.getDateTime().compareTo(d1.getDateTime());
    }
}