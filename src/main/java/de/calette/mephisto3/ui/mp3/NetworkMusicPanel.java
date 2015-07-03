package de.calette.mephisto3.ui.mp3;

import callete.api.Callete;
import de.calette.mephisto3.ui.ControllablePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Well, this panel is not used!
 */
public class NetworkMusicPanel extends ControllablePanel {
  private final static Logger LOG = LoggerFactory.getLogger(NetworkMusicPanel.class);

  public NetworkMusicPanel() {
  }

  //--------------- Helper ------------------------------------------------------

  public boolean loadMusic() {
    try {
      return Callete.getNetworkMusicService().authenticate();
    } catch (Exception e) {
      LOG.error("Error creating music dict: " + e.getMessage(), e);
    }
    return false;
  }
}
