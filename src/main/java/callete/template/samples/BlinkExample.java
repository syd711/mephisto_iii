package callete.template.samples;


import callete.api.Callete;
import callete.api.services.gpio.DigitalOutputPin;
import callete.api.services.gpio.GPIOService;
import callete.api.services.gpio.PinState;

/**
 * Simple GPIO sample to let an LED blink for 10 seconds.
 */
public class BlinkExample {

  public static void main(String[] args) throws InterruptedException {
    GPIOService gpioService = Callete.getGPIOService();
    DigitalOutputPin digitalOutputPin = gpioService.connectDigitalOutputPin(12, PinState.HIGH);
    for(int i=0; i<30; i++) {
      //wait for
      Thread.sleep(700);
      digitalOutputPin.toggle();
    }
    System.exit(0);
  }
}
