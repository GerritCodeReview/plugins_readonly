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

import com.google.gerrit.server.config.SitePaths;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

@Singleton
public class ReadOnlyState {
  private static final String GERRIT_READONLY = "gerrit.readonly";

  private final File marker;

  @Inject
  ReadOnlyState(SitePaths sitePaths) {
    this.marker = sitePaths.etc_dir.resolve(GERRIT_READONLY).toFile();
  }

  public boolean isReadOnly() {
    return marker.exists();
  }

  public void setReadOnly(boolean readOnly) throws IOException {
    if (readOnly && !marker.exists()) {
      Files.newOutputStream(marker.toPath(), StandardOpenOption.CREATE).close();
    } else if (!readOnly && marker.exists()) {
      Files.delete(marker.toPath());
    }
  }
}
