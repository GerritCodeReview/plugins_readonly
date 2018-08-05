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

package com.googlesource.gerrit.plugins.readonly;

import com.google.gerrit.extensions.registration.DynamicSet;
import com.google.gerrit.extensions.webui.GwtPlugin;
import com.google.gerrit.extensions.webui.WebUiPlugin;
import com.google.gerrit.httpd.AllRequestFilter;
import com.google.gerrit.httpd.plugins.HttpPluginModule;
import com.google.inject.Scopes;

public class HttpModule extends HttpPluginModule {
  @Override
  protected void configureServlets() {
    install(ReadOnlyServlet.module());
    DynamicSet.bind(binder(), AllRequestFilter.class).to(ReadOnly.class).in(Scopes.SINGLETON);
    DynamicSet.bind(binder(), WebUiPlugin.class).toInstance(new GwtPlugin("readonly"));
  }
}
