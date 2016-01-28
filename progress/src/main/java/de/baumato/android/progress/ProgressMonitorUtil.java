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
package de.baumato.android.progress;


import android.os.Looper;

public class ProgressMonitorUtil {

  private ProgressMonitorUtil(){}

  public static <T> T checkNotNull(T reference, String errorMessage) {
    if (reference == null) {
      throw new NullPointerException(errorMessage);
    } else {
      return reference;
    }
  }

  public static String nullToEmpty(String s) {
    return s == null ? "" : s;
  }

  public static boolean equal( Object a, Object b) {
    return a == b || a != null && a.equals(b);
  }

  public static boolean isMainThread() {
    return Thread.currentThread() == Looper.getMainLooper().getThread();
  }

  public static void checkIsMainThread() {
    if (!isMainThread()) {
      throw new IllegalStateException("This method must be called from the main thread.");
    }
  }

}
