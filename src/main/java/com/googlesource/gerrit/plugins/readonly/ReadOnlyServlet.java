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

import static com.googlesource.gerrit.plugins.readonly.Constants.ENDPOINT;
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;

import com.google.gerrit.httpd.plugins.HttpPluginModule;
import com.google.gerrit.server.CurrentUser;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ReadOnlyServlet extends HttpServlet {
  private static final Logger log = LoggerFactory.getLogger(ReadOnlyServlet.class);
  private static final long serialVersionUID = -1L;

  private final Provider<CurrentUser> user;
  private final ReadOnlyState state;

  public static HttpPluginModule module() {
    return new HttpPluginModule() {
      @Override
      protected void configureServlets() {
        serve(ENDPOINT).with(ReadOnlyServlet.class);
      }
    };
  }

  @Inject
  ReadOnlyServlet(Provider<CurrentUser> user, ReadOnlyState state) {
    this.user = user;
    this.state = state;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse rsp) {
    if (state.isReadOnly()) {
      sendError(rsp, SC_SERVICE_UNAVAILABLE);
      return;
    }
    rsp.setStatus(SC_NO_CONTENT);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse rsp) {
    if (!user.get().getCapabilities().canAdministrateServer()) {
      sendError(rsp, SC_FORBIDDEN);
      return;
    }
    try {
      state.setReadOnly(true);
      rsp.setStatus(SC_NO_CONTENT);
    } catch (IOException e) {
      log.error("Failed to enable read-only mode", e);
      sendError(rsp, SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse rsp) {
    if (!user.get().getCapabilities().canAdministrateServer()) {
      sendError(rsp, SC_FORBIDDEN);
      return;
    }
    try {
      state.setReadOnly(false);
      rsp.setStatus(SC_NO_CONTENT);
    } catch (IOException e) {
      log.error("Failed to disable read-only mode", e);
      sendError(rsp, SC_INTERNAL_SERVER_ERROR);
    }
  }

  private static void sendError(HttpServletResponse rsp, int statusCode) {
    try {
      rsp.sendError(statusCode);
    } catch (IOException e) {
      rsp.setStatus(SC_INTERNAL_SERVER_ERROR);
      log.error("Failed to send error response", e);
    }
  }
}
