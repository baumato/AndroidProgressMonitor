[ ![Download](https://api.bintray.com/packages/baumato/maven/de.baumato.android.progress/images/download.svg) ](https://bintray.com/baumato/maven/de.baumato.android.progress/_latestVersion)
[ ![License](https://img.shields.io/badge/license-Eclipse-brightgreen.svg) ](http://choosealicense.com/licenses/epl-1.0/)


# AndroidProgressMonitor

This android library allows to smoothly report progress of tasks.

It is very helpful in situations when
 * some of the work is optional.
 * a lot of elements needs to br processed in a loop.
 * reporting logarithmic progress in case the number of ticks cannot be easily computed in advance.
 * the task should be cancellable easily.

This library adapts the clever [ProgressMonitor](https://git.io/vz9n0) and [SubMonitor](https://git.io/vz9n1)
 of eclipse equinox to android.

**Table of Contents**
- [Example app](#example-app)
- [Examples](#examples)
	- [Simple example](#simple-example)
	- [Condition example](#condition-example)
	- [Loop example](#loop-example)
	- [Unknown number of elements example](#unknown-number-of-elements-example)
- [Usage](#usage)
- [License](#license)

## Example app

An example app showing different use cases is included in this repository.

![ProgressExample](https://raw.githubusercontent.com/baumato/AndroidProgressMonitor/master/doc/ProgressExample.gif)

## Examples

All following examples assume that you have an activity with a ProgressBar and optionally a
 TextField that will show the name of the task you want to monitor.

In your activity, add following members:

```java
ProgressMonitor progressMonitor;
TextView txtMsg;
ProgressBar progressBar;
```

Create a ProgressBarMonitor somewhere (e.g. onCreate method) and pass it to your async task:

```java
progressBar = (ProgressBar) findViewById(R.id.progressBar);
txtMsg = (TextView) findViewById(R.id.txtMsg);
progressMonitor = ProgressBarMonitor.of(progressBar, txtMsg);
```

Somewhere start your async task like:

```java
new SimpleExample(progressMonitor).execute();
```

### Simple example

```java
@Override
protected String doInBackground(Void... params) {
  // Convert the monitor into a SubMonitor progress instance, no need to call beginTask.
  // 100 is the total number of work units into which the main task gets subdivided.
  // Eventually call the monitor's done method.
  SubMonitor progress = SubMonitor.convert(monitor, "Simple SubMonitor Example", 100);
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
```

### Condition example

This example demonstrates how to smoothly report progress in situations where some of the work is optional. This task is not cancellable.

```java
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
```

### Loop example

```java
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
```

### Unknown number of elements example

This example demonstrates how to report logarithmic progress in situations where the number of ticks cannot be easily computed in advance.

```java
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
```

## Usage

[ ![Download](https://api.bintray.com/packages/baumato/maven/de.baumato.android.progress/images/download.svg) ](https://bintray.com/baumato/maven/de.baumato.android.progress/_latestVersion)

or add following line to your dependencies in your app's build.gradle file (it is available in jcenter):

```gradle
compile 'de.baumato.android.progress:progress:1.3.0@aar'
```

## License

[ ![License](https://img.shields.io/badge/license-Eclipse-brightgreen.svg) ](http://choosealicense.com/licenses/epl-1.0/)

All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html.

* [Eclipse Public License - v 1.0](https://www.eclipse.org/legal/epl-v10.html)
* [Short license explanation on choosealicense.com](http://choosealicense.com/licenses/epl-1.0/)
* [EPL FAQ](https://eclipse.org/legal/eplfaq.php)
