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
import static com.googlesource.gerrit.plugins.readonly.Constants.GIT_UPLOAD_PACK_PROTOCOL;
import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;

import com.google.common.collect.ImmutableList;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.httpd.AllRequestFilter;
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

@Singleton
class ReadOnly extends AllRequestFilter implements CommitValidationListener {
  private final ReadOnlyState state;
  private final ReadOnlyConfig config;
  private final String endpoint;
  private final String pluginName;

  @Inject
  ReadOnly(ReadOnlyState state, ReadOnlyConfig config, @PluginName String pluginName) {
    this.state = state;
    this.config = config;
    this.endpoint = pluginName + ENDPOINT;
    this.pluginName = pluginName;
  }

  @Override
  public List<CommitValidationMessage> onCommitReceived(CommitReceivedEvent receiveEvent)
      throws CommitValidationException {
    if (state.isReadOnly()) {
      throw new CommitValidationException(config.message());
    }
    return ImmutableList.of();
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    if (state.isReadOnly()
        && request instanceof HttpServletRequest
        && response instanceof HttpServletResponse
        && shouldBlock((HttpServletRequest) request)) {
      HttpServletRequest servletRequest = (HttpServletRequest) request;
      String servletPath = servletRequest.getServletPath();
      // Allow all requests from this plugin
      if (!servletPath.equals(String.format("/config/server/%s~readonly", pluginName))) {
        ((HttpServletResponse) response).sendError(SC_SERVICE_UNAVAILABLE, config.message());
        return;
      }
    }
    chain.doFilter(request, response);
  }

  private boolean shouldBlock(HttpServletRequest request) {
    String method = request.getMethod();
    String uri = request.getRequestURI();
    return !uri.endsWith(endpoint)
        && (("POST".equals(method) && !uri.endsWith(GIT_UPLOAD_PACK_PROTOCOL))
            || "PUT".equals(method)
            || "DELETE".equals(method));
  }
}
