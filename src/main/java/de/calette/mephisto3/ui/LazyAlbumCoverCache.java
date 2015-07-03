package de.calette.mephisto3.ui;

import callete.api.services.impl.music.google.AlbumCoverCache;
import callete.api.services.music.model.Album;
import de.calette.mephisto3.resources.ResourceLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This cache loads images only for the given collection of albums.
 * If the collection changes, the image are forgotten to handle
 * the limited amount of memory on the embedded system.
 */
public class LazyAlbumCoverCache {
  private final static Logger LOG = LoggerFactory.getLogger(LazyAlbumCoverCache.class);

  private static AlbumLoader loader;
  private static int width = AlbumBox.COVER_WIDTH;
  private static int height = AlbumBox.COVER_HEIGHT;

  private static Map<Album, ImageView> cache = new HashMap<>();

  public static void loadImageViewFor(BorderPane pane, Album album) {
    if(loader != null && loader.running) {
      try {
        synchronized(loader) {
          LOG.info("Waiting on album loader thread...");
          loader.wait(); //TODO
          LOG.info("Continuing to build UI after cover has been loaded.");
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    pane.setCenter(cache.get(album));
  }

  public static void load(Collection<Album> albumCollection) {
    cache.clear();
    if(loader != null && loader.isAlive()) {
      loader.setRunning(false);
    }
    loader = new AlbumLoader(albumCollection);
    loader.start();
  }

  /**
   * Asynchronous loading of images
   */
  static class AlbumLoader extends Thread {

    private Collection<Album> albums;
    private boolean running = true;

    private AlbumLoader(Collection<Album> albums) {
      this.albums = albums;
    }

    @Override
    public void run() {
      Thread.currentThread().setName("AlbumLoader/" + Thread.currentThread().getName());
      LOG.info("Starting new album cover loading thread for " + albums.size() + " images");
      for(Album album : albums) {
        String url = AlbumCoverCache.loadCover(album);
        if(url != null) {
          ImageView cover = new ImageView(new Image(url, width, height, false, true));
          cache.put(album, cover);
        }
        else {
          ImageView cover = new ImageView(new Image(ResourceLoader.getResource("folder.png"), width, height, false, true));
          cache.put(album, cover);
        }
        if(!running) {
          break;
        }
      }
      LOG.info("Finished loading album covers, notifying waiting threads.");
      this.running = false;
      synchronized(this) {
        notifyAll();
      }
    }

    public void setRunning(boolean running) {
      this.running = running;
    }
  }
}
