package de.baumato.android.progress.app;

import android.os.AsyncTask;

import de.baumato.android.progress.OperationCanceledException;
import de.baumato.android.progress.ProgressMonitor;

import static de.baumato.android.progress.app.ExampleUtil.simulateHardWork;

/**
 * <p>
 * This class demonstrates the simplest usage of the {@link ProgressMonitor} which is not
 * really better than publishing progress with the usual android api {@link #publishProgress}.
 * The real fun begins with the {@link de.baumato.android.progress.SubMonitor}.
 * </p>
 *
 * @see SimpleSubMonitorExample
 * @see ConditionExample
 * @see LoopExample
 * @see UnknownNumberOfElementsExample
 */
class SimpleExample extends AsyncTask<Void, Void, String> {

  private final ProgressMonitor monitor;

  SimpleExample(ProgressMonitor monitor) {
    this.monitor = monitor;
  }

  @Override
  protected String doInBackground(Void... params) {
    // Convert the monitor into a SubMonitor progress instance, no need to call beginTask.
    // 100 is the total number of work units into which the main task gets subdivided.
    // Eventually call the monitor's done method.
    monitor.beginTask("Background-Job", 100);
    try {
      for (int i = 1; i <= 10; i++) {
        // Notifies that a subtask of the main task is beginning. The subtask name gets appended
        // to the main task name.
        monitor.subTask("Subtask " + i);

        simulateHardWork();

        // optionally check if user cancelled the task, see ProgressMonitors#setCancel
        if (monitor.isCanceled()) {
          throw new OperationCanceledException();
        }

        // Notify that another 10% of the work is done.
        monitor.worked(10);
      }
      return "done";

    } catch (OperationCanceledException e) {
      return "cancelled";

    } finally {
      // Notifies that the work is done;that is, either the main task is completed or the user canceled it.
      monitor.done();
    }
  }
}
