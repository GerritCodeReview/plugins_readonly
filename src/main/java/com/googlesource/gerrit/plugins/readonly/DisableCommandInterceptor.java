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

import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.sshd.SshCreateCommandInterceptor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DisableCommandInterceptor implements SshCreateCommandInterceptor {
  private static final Logger log = LoggerFactory.getLogger(DisableCommandInterceptor.class);
  private static final String PATTERN = "^gerrit plugin (\\brm\\b|\\bremove\\b|\\breload\\b) %s$";

  private final String pluginName;
  private final Pattern pattern;

  @Inject
  DisableCommandInterceptor(@PluginName String pluginName) {
    this.pluginName = pluginName;
    this.pattern = Pattern.compile(String.format(PATTERN, pluginName));
  }

  @Override
  public String intercept(String in) {
    if (pattern.matcher(in).matches()) {
      return in;
    }

    log.warn("Disabling command: {}", in);
    return pluginName + " disable";
  }
}
