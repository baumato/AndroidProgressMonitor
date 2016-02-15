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

import java.util.Arrays;
import java.util.Collection;

import de.baumato.android.progress.ProgressMonitor;
import de.baumato.android.progress.SubMonitor;

import static de.baumato.android.progress.app.ExampleUtil.simulateHardWork;

/**
 * This example demonstrates how to report progress in a loop.
 */
public class LoopExample extends AsyncTask<Void, Void, String> {

  private final ProgressMonitor monitor;

  LoopExample(ProgressMonitor monitor) {
    this.monitor = monitor;
  }

  @Override
  protected String doInBackground(Void... params) {
    // Convert the monitor into a SubMonitor progress instance, no need to call beginTask.
    // 100 is the total number of work units into which the main task is been subdivided.
    SubMonitor progress = SubMonitor.convert(monitor, "Loop Example", 100);
    try {
      Collection<String> elements = createElements();

      // Create a new progress monitor that uses 70% of the total progress and will allocate one
      // tick for each element of the given collection.
      SubMonitor loopProgress = progress.split(70).setWorkRemaining(elements.size());

      for (String elem : elements) {
        doWorkOnElement(elem, loopProgress.split(1));
      }

      // Use the remaining 30% of the progress monitor to do some work outside the loop
      return doSomethingElse(progress.split(30));
    } finally {
      monitor.done();
    }
  }

  private Collection<String> createElements() {
    return Arrays.asList("a", "lot", "of", "elements", "here");
  }

  private void doWorkOnElement(String elem, SubMonitor monitor) {
    // Notifies that a subtask of the main task is beginning.
    // The subtask name gets appended to the main task name
    monitor.subTask("Element " + elem);
    simulateHardWork();
  }

  private String doSomethingElse(SubMonitor progress) {
    simulateHardWork();
    progress.worked(10);
    simulateHardWork();
    progress.worked(10);
    simulateHardWork();
    progress.worked(10);
    return "done";
  }
}
