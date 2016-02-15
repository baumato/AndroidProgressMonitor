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
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.baumato.android.progress.OperationCanceledException;
import de.baumato.android.progress.ProgressMonitor;
import de.baumato.android.progress.SubMonitor;

import static de.baumato.android.progress.app.ExampleUtil.*;

/**
 * <p>
 * This example demonstrates how to report logarithmic progress in situations where the number
 * of ticks cannot be easily computed in advance.
 * </p>
 * @see SimpleSubMonitorExample
 * @see ConditionExample
 * @see LoopExample
 */
class UnknownNumberOfElementsExample extends AsyncTask<Void, Void, String> {

  private final ProgressMonitor monitor;

  UnknownNumberOfElementsExample(ProgressMonitor monitor) {
    this.monitor = monitor;
  }

  @Override
  protected String doInBackground(Void... params) {
    try {
      SubMonitor progress = SubMonitor.convert(monitor, "Unknown number of elements example", 100);

      Iterator<String> unknownNumberOfElemsIter = createIterator();

      // creating the iterator is 10% of the task.
      progress.worked(10);

      // looping thru the unknown number of elements is 80% of the work.
      SubMonitor loopProgress = progress.split(80);

      while (unknownNumberOfElemsIter.hasNext()) {

        String elem = unknownNumberOfElemsIter.next();

        /*
         * Regardless of the amount of progress reported so far,
         * use 0.01% of the space remaining in the monitor to process the next element.
         */
        loopProgress.setWorkRemaining(10000);

        /*
         * Creates a sub progress monitor that will consume the given number of ticks from the
         * receiver. split will check for cancellation and will throw an OperationCanceledException
         * if the monitor has been cancelled. If no cancellation check is needed, use newChild
         * method.
         */
        doWorkOnElement(elem, loopProgress.split(1));
      }

      // calling split on progress automatically finishes the loopProgress.
      // do something else are the last 10% of the work
      return doSomethingElse(progress.split(10));

    } catch (OperationCanceledException e) {
      return "cancelled";
    } finally {
      monitor.done();
    }
  }

  private Iterator<String> createIterator() {
    List<String> res = new ArrayList<>();
    int count = random(10000, 50000);
    Log.d(UnknownNumberOfElementsExample.class.getName(), "createiterator count=" + count);
    for (int i = 0; i < count; i++) {
      res.add(String.valueOf(i+1));
    }
    return res.iterator();
  }

  private int random(int minIncl, int maxIncl) {
    int maxExcl = maxIncl + 1;
    return (int) (Math.random() * (maxExcl - minIncl) + minIncl);
  }

  private void doWorkOnElement(String element, SubMonitor progress) {
    try {
      TimeUnit.NANOSECONDS.sleep(1);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private String doSomethingElse(SubMonitor progress) {
    simulateHardWork();
    return "done";
  }
}
