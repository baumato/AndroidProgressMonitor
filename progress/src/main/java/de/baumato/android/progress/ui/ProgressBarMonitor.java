/*
 * Copyright (c) 2016 Tobias Baumann
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p>
 * Contributors:
 *     Tobias Baumann - initial API and implementation
 */
package de.baumato.android.progress.ui;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.baumato.android.progress.ProgressMonitor;

import static de.baumato.android.progress.ProgressMonitorUtil.checkNotNull;
import static de.baumato.android.progress.ProgressMonitorUtil.isMainThread;
import static de.baumato.android.progress.ProgressMonitorUtil.nullToEmpty;


/**
 * This ProgressMonitor implementation wraps an ProgressBar and optionally a TextView
 * to show task and subtask names. This implementation of ProgressMonitor allows to monitor
 * progress more than once with the same instance.
 * All ProgressMonitor methods may be called from any thread, every call gets transferred to
 * the main thread.
 * For monitoring progress of an activity that is not trivial the SubMonitor should be used.
 *
 * @see de.baumato.android.progress.SubMonitor
 */
public class ProgressBarMonitor implements ProgressMonitor {

  private final Handler handler;
  private final ProgressBar progressBar;
  private final TextView messageTextView;

  private String mainTaskName = "";
  private String fullTaskName = "";
  private String currentTaskName = "";

  private volatile Stopwatch stopwatch = new Stopwatch();
  private volatile boolean canceled;

  /**
   * Creates and returns a new ProgressBarMonitor that wraps the given progress bar.
   *
   * @param progressBar the progress bar to wrap
   * @return a new ProgressBarMonitor instance
   */
  public static ProgressBarMonitor of(ProgressBar progressBar) {
    return new ProgressBarMonitor(progressBar, null);
  }

  /**
   * Creates and returns a new ProgressBarMonitor that wraps the given progress bar and text view.
   *
   * @param progressBar     the progress bar to wrap.
   * @param messageTextView the text view to show the task and subtask names
   * @return a new ProgressBarMonitor instance.
   */
  public static ProgressBarMonitor of(ProgressBar progressBar, TextView messageTextView) {
    return new ProgressBarMonitor(progressBar, messageTextView);
  }

  /**
   * Constructs a new ProgressBarMonitor.
   *
   * @param pb  the ProgressBar to show the activities progress, not null
   * @param txt the TextView to show the current task name, may be null
   */
  private ProgressBarMonitor(ProgressBar pb, TextView txt) {
    this.handler = new Handler(Looper.getMainLooper());
    this.progressBar = checkNotNull(pb, "Given progress bar must not be null.");
    this.messageTextView = txt;
  }

  /**
   * Returns the progress bar given in the constructor. Should only be called from the main thread.
   *
   * @return the progress bar
   */
  public ProgressBar getProgressBar() {
    return this.progressBar;
  }

  /**
   * Return the text view given in the constructor. Should only be called from the main thread.
   *
   * @return the text view
   */
  public TextView getMessageTextView() {
    return this.messageTextView;
  }

  /**
   * Returns the duration of the last activity measured from #beginTask to the current time
   * in milliseconds. If #done already has been called the duration gets not updated anymore.
   * This method returns -1 if no activity has been started with #beginTask yet.
   *
   * @return the duration in milliseconds
   */
  public long getDurationInMillis() {
    return stopwatch.getDurationInMillis();
  }

  /**
   * Notifies that the main task is beginning.
   *
   * @param totalWork the total number of work units into which the main task is been subdivided.
   *                  If the value is <code>UNKNOWN</code> then the progress bar gets indeterminate
   *                  <code>ProgressBar.setIndeterminate(true)</code>
   */
  @Override
  public void beginTask(int totalWork) {
    beginTask("", totalWork);
  }

  /**
   * Notifies that the main task is beginning.
   *
   * @param name      the name (or description) of the main task
   * @param totalWork the total number of work units into which the main task is been subdivided.
   *                  If the value is <code>UNKNOWN</code> then the progress bar gets indeterminate
   *                  <code>ProgressBar.setIndeterminate(true)</code>
   */
  @Override
  public void beginTask(final String name, final int totalWork) {
    stopwatch = new Stopwatch().start();
    runOnMainThread(new Runnable() {
      @Override
      public void run() {
        beginTaskInMainThread(name, totalWork);
      }
    });
  }

  private void runOnMainThread(Runnable runnable) {
    if (isMainThread()) {
      runnable.run();
    } else {
      boolean added = handler.post(runnable);
      if (!added) {
        throw new IllegalStateException("Unexpected state. Probably the looper processing the message queue is exiting");
      }
    }
  }

  private void beginTaskInMainThread(String name, int totalWork) {
    final boolean indeterminate = (totalWork == UNKNOWN || totalWork == 0);
    progressBar.setIndeterminate(indeterminate);
    if (!indeterminate) {
      progressBar.setProgress(0);
      progressBar.setMax(totalWork);
    }
    setTaskNameInMainThread(name);
  }

  @Override
  public void setTaskName(final String name) {
    runOnMainThread(new Runnable() {
      @Override
      public void run() {
        setTaskNameInMainThread(name);
      }
    });
  }

  /**
   * Sets the task name to the given value. This method is used to
   * restore the task label after a nested operation was executed.
   * <b>Normally there is no need for clients to call this method.</b>
   *
   * @param name the name (or description) of the main task
   * @see #beginTask(String, int)
   */
  private void setTaskNameInMainThread(String name) {
    this.mainTaskName = nullToEmpty(name);
    this.fullTaskName = this.mainTaskName;
  }

  private void updateMessageTextView() {
    if (messageTextView != null && !currentTaskName.equals(fullTaskName)) {
      Log.d(ProgressBarMonitor.class.getName(), "updateMessageTextView with " + fullTaskName);
      this.currentTaskName = fullTaskName;
      this.messageTextView.setText(currentTaskName);
    }
  }

  /**
   * Notifies that a subtask of the main task is beginning.
   * Subtasks are optional; the main task might not have subtasks.
   *
   * @param name the name (or description) of the subtask
   */
  @Override
  public void subTask(final String name) {
    runOnMainThread(new Runnable() {
      @Override
      public void run() {
        subTaskInMainThread(name);
      }
    });
  }

  private void subTaskInMainThread(String name) {
    final String subTaskName = nullToEmpty(name);
    final String newFullTaskName;
    if (subTaskName.isEmpty()) {
      newFullTaskName = mainTaskName;
    } else if (mainTaskName.isEmpty()) {
      newFullTaskName = subTaskName;
    } else {
      newFullTaskName = mainTaskName + ": " + subTaskName;
    }
    this.fullTaskName = newFullTaskName;
    updateMessageTextView();
  }

  /**
   * Notifies that the work is done; that is, either the main task is completed
   * or the user canceled it. This method may be called more than once.
   */
  @Override
  public void done() {
    stopwatch = stopwatch.stop();
    runOnMainThread(new Runnable() {
      @Override
      public void run() {
        doneInMainThread();
      }
    });
  }

  private void doneInMainThread() {
    this.progressBar.setProgress(progressBar.getMax());
    setTaskNameInMainThread(null);
    updateMessageTextView();
  }

  /**
   * Returns whether cancellation of current operation has been requested. Long-running operations
   * may poll to see if cancellation has been requested. For an easier cancellation handling, see
   * {@link de.baumato.android.progress.SubMonitor}
   *
   * @return <code>true</code> if cancellation has been requested,
   * and <code>false</code> otherwise
   * @see #setCanceled(boolean)
   * @see de.baumato.android.progress.SubMonitor#split(int)
   */
  @Override
  public boolean isCanceled() {
    return canceled;
  }

  /**
   * Sets the cancel state to the given value.
   *
   * @param value <code>true</code> indicates that cancellation has
   *              been requested (but not necessarily acknowledged);
   *              <code>false</code> clears this flag
   * @see #isCanceled()
   */
  @Override
  public void setCanceled(boolean value) {
    this.canceled = value;
  }

  /**
   * Notifies that a given number of work unit of the main task
   * has been completed. Note that this amount represents an
   * installment, as opposed to a cumulative amount of work done
   * to date. For a more sophisticated behaviour see {@link de.baumato.android.progress.SubMonitor}.
   *
   * @param work a non-negative number of work units just completed
   */
  @Override
  public void worked(final int work) {
    runOnMainThread(new Runnable() {
      @Override
      public void run() {
        workedInMainThread(work);
      }
    });
  }

  private void workedInMainThread(int work) {
    String msg = "workedInMainThread with " + progressBar.getProgress() + "/" + progressBar.getMax();
    Log.d(ProgressBarMonitor.class.getName(), msg);
    updateMessageTextView();
    progressBar.incrementProgressBy(work);
  }

}
