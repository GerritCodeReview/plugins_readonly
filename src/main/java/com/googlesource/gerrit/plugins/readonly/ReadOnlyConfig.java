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

import com.google.common.collect.ImmutableSet;
import com.google.gerrit.extensions.annotations.PluginName;
import com.google.gerrit.server.config.PluginConfigFactory;
import com.google.gerrit.server.config.SitePaths;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.nio.file.Path;
import java.util.Set;
import org.eclipse.jgit.lib.Config;

@Singleton
class ReadOnlyConfig {
  private static final String MESSAGE_KEY = "message";
  private static final String MARKER_DIR_PATH_KEY = "markerDir";
  private static final String DEFAULT_MESSAGE =
      "Gerrit is under maintenance - all data is READ ONLY";
  private static final String SSH_ALLOW = "allowSshCommand";

  private final String message;
  private final Path markerDir;
  private final Set<String> allowSshCommands;

  @Inject
  ReadOnlyConfig(
      SitePaths sitePaths, PluginConfigFactory pluginConfigFactory, @PluginName String pluginName) {
    Config cfg = pluginConfigFactory.getGlobalPluginConfig(pluginName);
    this.message = firstNonNull(cfg.getString(pluginName, null, MESSAGE_KEY), DEFAULT_MESSAGE);
    this.markerDir =
        sitePaths.resolve(
            firstNonNull(cfg.getString(pluginName, null, MARKER_DIR_PATH_KEY), "etc"));
    this.allowSshCommands = ImmutableSet.copyOf(cfg.getStringList(pluginName, null, SSH_ALLOW));
  }

  String message() {
    return message;
  }

  Path markerDir() {
    return markerDir;
  }

  Set<String> allowSshCommands() {
    return allowSshCommands;
  }
}
