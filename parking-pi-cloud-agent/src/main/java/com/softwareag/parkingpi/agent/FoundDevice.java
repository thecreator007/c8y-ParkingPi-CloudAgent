package com.softwareag.parkingpi.agent;

import c8y.Hardware;
import com.cumulocity.model.idtype.GId;
import com.cumulocity.rest.representation.inventory.ManagedObjectRepresentation;
import com.cumulocity.sdk.client.inventory.InventoryApi;
import com.cumulocity.sdk.client.inventory.InventoryFilter;
import com.cumulocity.sdk.client.inventory.ManagedObjectCollection;

public class FoundDevice {

   public GId discoverParentID(InventoryApi inventory) {
        InventoryFilter ivf = new InventoryFilter();
        ivf.byFragmentType(Hardware.class);
        GId ParentGId= new GId();
        ManagedObjectCollection moc = inventory.getManagedObjectsByFilter(ivf);
        for (ManagedObjectRepresentation mo : moc.get().allPages()) {
            GId id = mo.getId();
            ManagedObjectRepresentation newManagedObj = inventory.get(GId.asGId(id));
            String serial = newManagedObj.get(Hardware.class).getSerialNumber();
            if (serial.equals("dhdon0tdelete")) {
                ParentGId=id;
                break;
            }
        }
        return ParentGId;
    }
}
