// Copyright (C) 2018 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.readonly.client;

import com.google.gerrit.client.rpc.NativeString;
import com.google.gerrit.plugin.client.Plugin;
import com.google.gerrit.plugin.client.rpc.NoContent;
import com.google.gerrit.plugin.client.rpc.RestApi;
import com.google.gerrit.plugin.client.screen.Screen;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReadOnlyScreen extends VerticalPanel {
  static class Factory implements Screen.EntryPoint {
    @Override
    public void onLoad(Screen screen) {
      screen.setPageTitle("Manage Read Only state");
      screen.show(new ReadOnlyScreen());
    }
  }

  ReadOnlyScreen() {
    setStyleName("readonly-panel");

    new RestApi("config")
        .id("server")
        .view(Plugin.get().getPluginName(), "state")
        .get(
            new AsyncCallback<NativeString>() {
              @Override
              public void onSuccess(final NativeString stateInfo) {
                display(stateInfo.asString());
              }

              @Override
              public void onFailure(Throwable caught) {
                // never invoked
              }
            });
  }

  private void display(String state) {
    MyTable t = new MyTable();
    t.setStyleName("readonly-readOnlyInfoTable");
    t.addRow("State", createStateToggle(state));
    add(t);
  }

  private ToggleButton createStateToggle(String state) {
    ToggleButton stateToggle = new ToggleButton();
    stateToggle.setStyleName("readonly-toggleButton");
    stateToggle.setValue(true);
    stateToggle.setText("On");
    stateToggle.setValue(false);
    stateToggle.setText("Off");
    stateToggle.setValue(state.equals("on"));
    stateToggle.setVisible(true);

    stateToggle.addValueChangeHandler(
        new ValueChangeHandler<Boolean>() {
          @Override
          public void onValueChange(ValueChangeEvent<Boolean> event) {
            if (event.getValue()) {
              new RestApi("config")
                  .id("server")
                  .view(Plugin.get().getPluginName(), "readonly")
                  .put(
                      new AsyncCallback<NoContent>() {
                        @Override
                        public void onSuccess(NoContent result) {}

                        @Override
                        public void onFailure(Throwable caught) {
                          // never invoked
                        }
                      });
            } else {
              new RestApi("config")
                  .id("server")
                  .view(Plugin.get().getPluginName(), "ready")
                  .put(
                      new AsyncCallback<NoContent>() {
                        @Override
                        public void onSuccess(NoContent result) {}

                        @Override
                        public void onFailure(Throwable caught) {
                          // never invoked
                        }
                      });
            }
          }
        });

    return stateToggle;
  }

  private static class MyTable extends FlexTable {
    private static int row = 0;

    private void addRow(String label, Widget w) {
      setWidget(row, 0, new Label(label + ":"));
      setWidget(row, 1, w);
      row++;
    }
  }
}
