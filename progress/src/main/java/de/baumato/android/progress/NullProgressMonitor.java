/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tobias Baumann - initial adaption of equinox NullProgressMonitor
 *                      to android, original file: https://git.io/vz9CO
 *******************************************************************************/
package de.baumato.android.progress;

/**
 * A default progress monitor implementation suitable for subclassing.
 * <p>
 * This implementation supports cancellation. The default implementations of the
 * other methods do nothing.
 * </p>
 */
public class NullProgressMonitor implements ProgressMonitor {

  /**
   * Indicates whether cancel has been requested.
   */
  private volatile boolean cancelled = false;

  /**
   * Constructs a new progress monitor.
   */
  public NullProgressMonitor() {
    super();
  }

  /**
   * Notifies that the main task is beginning.  This must only be called once
   * on a given progress monitor instance.
   *
   * @param totalWork the total number of work units into which
   *                  the main task is been subdivided. If the value is <code>UNKNOWN</code>
   *                  the implementation is free to indicate progress in a way which
   *                  doesn't require the total number of work units in advance.
   */
  @Override
  public void beginTask(int totalWork) {
    // do nothing
  }

  /**
   * This implementation does nothing.
   * Subclasses may override this method to do interesting
   * processing when a task begins.
   *
   * @see ProgressMonitor#beginTask(String, int)
   */
  @Override
  public void beginTask(String name, int totalWork) {
    // do nothing
  }

  /**
   * This implementation does nothing.
   * Subclasses may override this method to do interesting
   * processing when a task is done.
   *
   * @see ProgressMonitor#done()
   */
  @Override
  public void done() {
    // do nothing
  }

  /**
   * This implementation returns the value of the internal
   * state variable set by <code>setCanceled</code>.
   * Subclasses which override this method should
   * override <code>setCanceled</code> as well.
   *
   * @see ProgressMonitor#isCanceled()
   * @see ProgressMonitor#setCanceled(boolean)
   */
  @Override
  public boolean isCanceled() {
    return cancelled;
  }

  /**
   * This implementation sets the value of an internal state variable.
   * Subclasses which override this method should override
   * <code>isCanceled</code> as well.
   *
   * @see ProgressMonitor#isCanceled()
   * @see ProgressMonitor#setCanceled(boolean)
   */
  @Override
  public void setCanceled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  /**
   * This implementation does nothing.
   * Subclasses may override this method to do something
   * with the name of the task.
   *
   * @see ProgressMonitor#setTaskName(String)
   */
  @Override
  public void setTaskName(String name) {
    // do nothing
  }

  /**
   * This implementation does nothing.
   * Subclasses may override this method to do interesting
   * processing when a subtask begins.
   *
   * @see ProgressMonitor#subTask(String)
   */
  @Override
  public void subTask(String name) {
    // do nothing
  }

  /**
   * This implementation does nothing.
   * Subclasses may override this method to do interesting
   * processing when some work has been completed.
   *
   * @see ProgressMonitor#worked(int)
   */
  @Override
  public void worked(int work) {
    // do nothing
  }
}
