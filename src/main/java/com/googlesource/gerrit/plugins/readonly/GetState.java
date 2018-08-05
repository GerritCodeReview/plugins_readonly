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

import static com.google.gerrit.common.data.GlobalCapability.ADMINISTRATE_SERVER;
import static com.google.gerrit.common.data.GlobalCapability.MAINTAIN_SERVER;

import com.google.gerrit.extensions.annotations.RequiresAnyCapability;
import com.google.gerrit.extensions.restapi.RestReadView;
import com.google.gerrit.server.config.ConfigResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@RequiresAnyCapability({ADMINISTRATE_SERVER, MAINTAIN_SERVER})
@Singleton
class GetState implements RestReadView<ConfigResource> {

  private final ReadOnlyState state;

  @Inject
  GetState(ReadOnlyState state) {
    this.state = state;
  }

  @Override
  public String apply(ConfigResource resource) {
    return state.isReadOnly() ? "on" : "off";
  }
}
