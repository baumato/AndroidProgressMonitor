package de.baumato.android.progress.app;

public class ExampleUtil {

  private ExampleUtil(){}

  static void simulateHardWork() {
    simulateHardWorkByWaitingMillis(1000);
  }

  static void simulateHardWorkByWaitingMillis(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
