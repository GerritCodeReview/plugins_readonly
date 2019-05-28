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

public class ReadOnlyByHttpIT extends AbstractReadOnlyTest {
  @Override
  protected void setReadOnly(boolean readOnly) throws Exception {
    String expectedStatus = readOnly ? "on" : "off";
    RestResponse response;
    if (readOnly) {
      response = adminRestSession.put("/config/server/readonly~readonly");
      response.assertOK();
      assertThat(response.getEntityContent()).contains(expectedStatus);
    } else {
      response = adminRestSession.delete("/config/server/readonly~readonly");
      response.assertOK();
      assertThat(response.getEntityContent()).contains(expectedStatus);
    }
    response = adminRestSession.get("/config/server/readonly~readonly");
    response.assertOK();
    assertThat(response.getEntityContent()).contains(expectedStatus);
  }
}
