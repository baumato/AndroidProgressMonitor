package de.baumato.android.progress.app;

import android.os.AsyncTask;

import de.baumato.android.progress.OperationCanceledException;
import de.baumato.android.progress.ProgressMonitor;
import de.baumato.android.progress.SubMonitor;

import static de.baumato.android.progress.app.ExampleUtil.simulateHardWork;
import static de.baumato.android.progress.app.ExampleUtil.simulateHardWorkByWaitingMillis;

/**
 * <p>
 * This method shows the simplest usage of the {@link SubMonitor}. The SubMonitor allows
 * a very flexible way of reporting progress.
 * </p>
 *
 * @see ConditionExample
 * @see LoopExample
 * @see UnknownNumberOfElementsExample
 */
class SimpleSubMonitorExample extends AsyncTask<Void, Void, String> {

  private final ProgressMonitor monitor;

  SimpleSubMonitorExample(ProgressMonitor monitor) {
    this.monitor = monitor;
  }

  @Override
  protected String doInBackground(Void... params) {
    // Convert the monitor into a SubMonitor progress instance, no need to call beginTask.
    // 100 is the total number of work units into which the main task gets subdivided.
    // Eventually call the monitor's done method.
    SubMonitor progress = SubMonitor.convert(monitor, "Background-Job", 100);
    try {
      // Use 30% of the progress to do some work.
      // split checks frequently if user has cancelled the operation.
      // Use newChild if you do not want to allow cancellations.
      doSomething(progress.split(30));

      // Advance the monitor by another 30%.
      progress.worked(30);

      // Use the remaining 40% of the progress to do some more work.
      return doSomethingElse(progress.split(40));

    } catch (OperationCanceledException e) {
      return "cancelled";

    } finally {
      // Notifies that the work is done;that is, either the main task is completed or the user canceled it.
      monitor.done();
    }
  }

  private void doSomething(SubMonitor progress) {
    simulateHardWork();
    progress.worked(10);
    simulateHardWork();
    progress.worked(10);
    simulateHardWork();
    progress.worked(10);
  }

  private String doSomethingElse(SubMonitor progress) {
    doSomethingElse(progress);
    simulateHardWork();
    progress.worked(10);
    return "done";
  }
}
