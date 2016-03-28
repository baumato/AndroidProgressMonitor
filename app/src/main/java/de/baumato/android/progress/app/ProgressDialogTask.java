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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import de.baumato.android.progress.OperationCanceledException;
import de.baumato.android.progress.ProgressMonitor;
import de.baumato.android.progress.SubMonitor;
import de.baumato.android.progress.ui.ProgressDialogMonitor;

class ProgressDialogTask extends AsyncTask<Void, Void, Void> {
  private final ProgressDialog dialog;
  private final ProgressMonitor monitor;

  public ProgressDialogTask(MainActivity activity) {
    dialog = new ProgressDialog(activity);
    dialog.setTitle("ProgressDialog");
    dialog.setMessage("Loading...");
    dialog.setIndeterminate(false);
    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    dialog.setCancelable(true);
    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialog) {
        monitor.setCanceled(true);
      }
    });
    monitor = ProgressDialogMonitor.of(dialog);
  }

  @Override
  protected void onPreExecute() {
    dialog.show();
  }

  @Override
  protected void onPostExecute(Void result) {
    if (dialog.isShowing()) {
      dialog.dismiss();
    }
  }

  @Override
  protected Void doInBackground(Void... params) {
    try {
      SubMonitor progress = SubMonitor.convert(monitor, "Loading...", 20);
      for (int i = 0; i < 20; i++) {
        if (i % 5 == 0) progress.subTask("SubTask " + i);
        ExampleUtil.simulateHardWorkByWaitingMillis(500);
        progress.split(1);
      }
    } catch (OperationCanceledException e) {
      toast("Cancelled.");
    }
    return null;
  }

  private void toast(final String msg) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(dialog.getContext(), msg, Toast.LENGTH_SHORT).show();
      }
    });
  }
}
