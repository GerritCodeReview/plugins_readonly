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

import static com.google.common.base.MoreObjects.firstNonNull;
import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.httpd.AllRequestFilter;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.events.CommitReceivedEvent;
import com.google.gerrit.server.git.validators.CommitValidationException;
import com.google.gerrit.server.git.validators.CommitValidationListener;
import com.google.gerrit.server.git.validators.CommitValidationMessage;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jgit.lib.Config;

@Singleton
class ReadOnly extends AllRequestFilter implements CommitValidationListener {
  private static final String MESSAGE_KEY = "message";
  private static final String DEFAULT_MESSAGE =
      "Gerrit is under maintenance - all data is READ ONLY";

  private final String message;

  @Inject
  ReadOnly(PluginConfigFactory pluginConfigFactory, @PluginName String pluginName) {
    Config cfg = pluginConfigFactory.getGlobalPluginConfig(pluginName);
    this.message = firstNonNull(cfg.getString(pluginName, null, MESSAGE_KEY), DEFAULT_MESSAGE);
  }

  @Override
  public List<CommitValidationMessage> onCommitReceived(CommitReceivedEvent receiveEvent)
      throws CommitValidationException {
    throw new CommitValidationException(message);
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse)) {
      String method = ((HttpServletRequest) request).getMethod();
      if (method == "POST" || method == "PUT" || method == "DELETE") {
        ((HttpServletResponse) response).sendError(SC_SERVICE_UNAVAILABLE, message);
        return;
      }
    }
    chain.doFilter(request, response);
  }
}
