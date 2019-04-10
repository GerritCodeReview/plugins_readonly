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
import com.google.gerrit.sshd.SshCommandPreExecutionFilter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class DisableCommand implements SshCommandPreExecutionFilter {
  private static final Logger log = LoggerFactory.getLogger(DisableCommand.class);
  private static final String PATTERN = "^gerrit plugin (\\brm\\b|\\bremove\\b) %s$";

  private final ReadOnlyState state;
  private final List<Pattern> allowPatterns = new ArrayList<>();
  private final List<String> allowPrefixes = new ArrayList<>();

  @Inject
  DisableCommand(
      @PluginName String pluginName, ReadOnlyConfig config, ReadOnlyState state) {
    this.state = state;
    allowPatterns.add(Pattern.compile(String.format(PATTERN, pluginName)));
    // Allow all SSH commands from this plugin
    allowPrefixes.add(pluginName);
    for (String allow : config.allowSshCommands()) {
      if (allow.startsWith("^")) {
        allowPatterns.add(Pattern.compile(allow));
      } else {
        allowPrefixes.add(allow);
      }
    }
  }

  @Override
  public boolean accept(String command, List<String> arguments) {
    if (!state.isReadOnly()
        || allowPrefixes.stream().anyMatch(p -> command.startsWith(p))
        || allowPatterns.stream().anyMatch(p -> p.matcher(command).matches())) {
      return false;
    }

    log.warn("Disabling command: {}", command);
    return true;
  }
}
