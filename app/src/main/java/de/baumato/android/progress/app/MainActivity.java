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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import de.baumato.android.progress.OperationCanceledException;
import de.baumato.android.progress.ProgressMonitor;
import de.baumato.android.progress.SubMonitor;
import de.baumato.android.progress.ui.ProgressBarMonitor;
import de.baumato.android.progress.ui.ProgressDialogMonitor;

public class MainActivity extends AppCompatActivity {

  private static final String LOG_TAG = MainActivity.class.getName();

  ProgressBarMonitor progressMonitor;
  TextView txtMsg;
  ProgressBar progressBar;
  final int[] radioButtonIds = new int[]{
          R.id.rbSimple,
          R.id.rbSubMonitorSimple,
          R.id.rbCondition,
          R.id.rbLoop,
          R.id.rbUnknownLoop,
          R.id.rbProgressDialog};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    progressBar = (ProgressBar) findViewById(R.id.progressBar);
    txtMsg = (TextView) findViewById(R.id.txtMsg);

    progressMonitor = ProgressBarMonitor.of(progressBar, txtMsg);

    Button btnStart = (Button) findViewById(R.id.btnStart);
    btnStart.setOnClickListener(new StartButtonClickListener());

    Button btnStop = (Button) findViewById(R.id.btnStop);
    btnStop.setOnClickListener(new StopButtonListener());
  }

  /**
   * Listener starting the selected example.
   */
  private class StartButtonClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {

      // setCanceled is usually not called, just for this example to reuse the progressMonitor
      progressMonitor.setCanceled(false);
      // start example
      int radioButtonId = readSelectedOption();
      switch (radioButtonId) {

        case R.id.rbSimple:
          new SimpleExample(progressMonitor).execute();
          break;

        case R.id.rbSubMonitorSimple:
          new SimpleSubMonitorExample(progressMonitor).execute();
          break;

        case R.id.rbCondition:
          new ConditionExample(progressMonitor).execute();
          break;

        case R.id.rbLoop:
          new LoopExample(progressMonitor).execute();
          break;

        case R.id.rbUnknownLoop:
          new UnknownNumberOfElementsExample(progressMonitor).execute();
          break;

        case R.id.rbProgressDialog:
          new ProgressDialogTask(MainActivity.this).execute();
          break;

        default:
          Log.i(LOG_TAG, "Unknown radioButtonId selected: " + radioButtonId);
      }
    }

    private int readSelectedOption() {
      for (int radioButtonId : radioButtonIds) {
        RadioButton rb = (RadioButton) findViewById(radioButtonId);
        if (rb.isChecked()) {
          return radioButtonId;
        }
      }
      return -1;
    }
  }

  /**
   * Listener that stops the current task by calling ProgressMonitor#setCanceled. The running
   * task may check for cancellation directly via ProgressMonitor#isCanceled or indirectly
   * via SubMonitor#split. SubMonitor#split throws a OperationCanceledException.
   */
  private class StopButtonListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
      progressMonitor.setCanceled(true);
    }
  }

  private class ShowProgressDialogListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
      ProgressDialogTask task = new ProgressDialogTask(MainActivity.this);
      task.execute();
    }
  }

}
