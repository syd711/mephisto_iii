package de.calette.mephisto3.control;

import callete.api.Callete;
import callete.api.services.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model that represents the current state of the UI.
 */
public class ServiceState {
  public final static String SETTING_SERVICE_NAME = "service.name";
  public final static String SETTING_SERVICE_SELECTION = "service.selection";

  private Service service;
  private List<?> models;
  private Map<Service, Integer> serviceIndex = new HashMap<>();

  public List<?> getModels() {
    return models;
  }

  public void setModels(List<?> models) {
    this.models = models;
  }

  public Service getService() {
    return service;
  }

  public void setService(Service service) {
    if(!serviceIndex.containsKey(service)) {
      serviceIndex.put(service, 0);
    }
    this.service = service;
  }

  /**
   * Returns the last active service model of
   * the service of this model.
   */
  public int getServiceIndex() {
    return serviceIndex.get(service);
  }

  /**
   * Remembers the last selected position for the service.
   */
  public void setServiceIndex(int pos) {
    serviceIndex.put(service, pos);
  }

  public void decrementIndex() {
    int index = getServiceIndex();
    if(index == 0) {
      index = models.size() - 1;
    }
    else {
      index--;
    }
    setServiceIndex(index);
//    System.out.println(service + " has index " + index);
  }

  public void incrementIndex() {
    int index = getServiceIndex();
    if(index == models.size() - 1) {
      index = 0;
    }
    else {
      index++;
    }
    setServiceIndex(index);
//    System.out.println(service + " has index " + index);
  }

  public Object getSelection() {
    return models.get(getServiceIndex());
  }

  public void saveState() {
    //Callete.saveSetting(SETTING_SERVICE_NAME, service.toString());
    Callete.saveSetting(SETTING_SERVICE_SELECTION, getServiceIndex());
  }
}
