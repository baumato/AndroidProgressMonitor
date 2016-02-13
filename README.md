# AndroidProgressMonitor

This android library allows to smoothly report progress of tasks.

It is very helpful in situations when
 * some of the work is optional.
 * a lot of elements needs to br processed in a loop.
 * reporting logarithmic progress in case the number of ticks cannot be easily computed in advance.

This library adapts the clever [ProgressMonitor](https://git.io/vz9n0) and [SubMonitor](https://git.io/vz9n1)
 of eclipse equinox to android.

# Examples

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

## Simple example
 
## Condition example

## Loop example

## Unknown number of elements example

# Usage

In your top level build file, add a repository containing this library:

```gradle
allprojects {
    repositories {
        jcenter()
        maven {
            url 'https://dl.bintray.com/baumato/maven'
        }
    }
}
```

Add following line to your dependencies in your app's build.gradle file:

```gradle
compile 'de.baumato.android.progress:progress:1.0.1@aar'
```
