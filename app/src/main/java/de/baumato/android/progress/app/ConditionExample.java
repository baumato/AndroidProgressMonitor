/*
 * Copyright (c) 2016 Tobias Baumann
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tobias Baumann - initial API and implementation
 *
 */

/*
 * Copyright (c) 2016 Tobias Baumann
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tobias Baumann - initial API and implementation
 *
 */
package de.baumato.android.progress.app;

import android.os.AsyncTask;

import de.baumato.android.progress.ProgressMonitor;
import de.baumato.android.progress.SubMonitor;

import static de.baumato.android.progress.app.ExampleUtil.simulateHardWorkByWaitingMillis;

/**
 * <p>
 * This example demonstrates how to smoothly report progress in situations where some of the
 * work is optional. This task is not cancellable.
 * </p>
 *
 * @see SimpleSubMonitorExample
 * @see LoopExample
 * @see UnknownNumberOfElementsExample
 */
class ConditionExample extends AsyncTask<Void, Void, String> {

  private final ProgressMonitor monitor;

  ConditionExample(ProgressMonitor monitor) {
    this.monitor = monitor;
  }

  @Override
  protected String doInBackground(Void... params) {
    // Convert the monitor into a SubMonitor progress instance, no need to call beginTask.
    // 100 is the total number of work units into which the main task gets subdivided.
    SubMonitor progress = SubMonitor.convert(monitor, "Condition Example", 100);
    try {
      if (condition()) {
        // Use 50% of the progress to do some work,
        doSomeWork(progress.newChild(50));
      }

      // Don't report any work, but ensure that we have 50 ticks remaining on the progress monitor.
      // If we already consumed 50 ticks in the above branch, this is a no-op.
      // Otherwise, the remaining  space in the monitor is redistributed into 50 ticks.
      progress.setWorkRemaining(50);

      // Use the remainder of the progress monitor to do the rest of the work.
      doSomeWork(progress.newChild(50));

    } finally {
      monitor.done();
    }
    return "done";
  }

  private boolean condition() {
    return false;
  }

  private void doSomeWork(ProgressMonitor monitor) {
    // This is a loop example.
    // Reserve another 5000 ticks for this monitor.
    SubMonitor progress = SubMonitor.convert(monitor, 5000);
    for (int i = 0; i < 50; i++) {
      simulateHardWorkByWaitingMillis(100);
      progress.worked(100);
    }
  }
}
