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

package de.baumato.android.progress.ui;

final class Stopwatch {

  private final long startTime;
  private final long currentTime;

  Stopwatch() {
    this(-1, -1);
  }

  private Stopwatch(long startTime, long currentTime) {
    this.startTime = startTime;
    this.currentTime = currentTime;
  }

  Stopwatch start() {
    return new Stopwatch(System.currentTimeMillis(), currentTime);
  }

  Stopwatch stop() {
    return new Stopwatch(startTime, System.currentTimeMillis());
  }

  long getDurationInMillis() {
    if (startTime == -1) {
      return -1;
    }
    if (currentTime == -1) {
      return System.currentTimeMillis() - startTime;
    }
    return currentTime - startTime;
  }
}
