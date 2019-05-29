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

import static com.google.common.truth.Truth.assertThat;

import com.google.gerrit.acceptance.RestResponse;
import com.google.gerrit.server.config.GerritServerConfig;
import com.google.gerrit.testing.ConfigSuite;
import com.google.inject.Inject;
import org.eclipse.jgit.lib.Config;

public class ReadOnlyByHttpIT extends AbstractReadOnlyTest {
  @ConfigSuite.Default
  public static Config withPluginNamePrefix() {
    Config cfg = new Config();
    cfg.setString("readonly", "test", "endpoint", "readonly~readonly");
    return cfg;
  }

  @ConfigSuite.Config
  public static Config withoutPluginNamePrefix() {
    Config cfg = new Config();
    cfg.setString("readonly", "test", "endpoint", "readonly");
    return cfg;
  }

  @Inject @GerritServerConfig private Config config;

  @Override
  protected void setReadOnly(boolean readOnly) throws Exception {
    String endpoint =
        String.format("/config/server/%s", config.getString("readonly", "test", "endpoint"));
    String expectedStatus = readOnly ? "on" : "off";
    RestResponse response;
    if (readOnly) {
      response = adminRestSession.put(endpoint);
      response.assertOK();
      assertThat(response.getEntityContent()).contains(expectedStatus);
    } else {
      response = adminRestSession.delete(endpoint);
      response.assertOK();
      assertThat(response.getEntityContent()).contains(expectedStatus);
    }
    response = adminRestSession.get(endpoint);
    response.assertOK();
    assertThat(response.getEntityContent()).contains(expectedStatus);
  }
}
