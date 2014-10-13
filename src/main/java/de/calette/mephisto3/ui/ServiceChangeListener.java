package de.calette.mephisto3.ui;

import de.calette.mephisto3.control.ServiceState;

/**
 * Interface to be implemented when the feature panel
 * controls another feature.
 */
public interface ServiceChangeListener {

  /**
   * Fired when the service has changed, passes
   * the new models so that the component can update.
   */
  void serviceChanged(ServiceState serviceState);
}
