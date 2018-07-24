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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DisableCommandInterceptor implements SshCreateCommandInterceptor {
  private static final Logger log = LoggerFactory.getLogger(DisableCommandInterceptor.class);
  private static final String PATTERN = "^gerrit plugin (\\brm\\b|\\bremove\\b) %s$";

  private final ReadOnlyConfig config;
  private final String disableCommand;
  private final List<Pattern> allowPatterns = new ArrayList<>();
  private final List<String> allowPrefixes = new ArrayList<>();

  @Inject
  DisableCommandInterceptor(@PluginName String pluginName, ReadOnlyConfig config) {
    this.config = config;
    this.disableCommand = pluginName + " disable";
    allowPatterns.add(Pattern.compile(String.format(PATTERN, pluginName)));
    for (String allow : config.allowSshCommands()) {
      if (allow.startsWith("^")) {
        allowPatterns.add(Pattern.compile(allow));
      } else {
        allowPrefixes.add(allow);
      }
    }
  }

  @Override
  public String intercept(String in) {
    if (!config.isReadOnly()
        || allowPrefixes.stream().anyMatch(p -> in.startsWith(p))
        || allowPatterns.stream().anyMatch(p -> p.matcher(in).matches())) {
      return in;
    }

    log.warn("Disabling command: {}", in);
    return disableCommand;
  }
}
