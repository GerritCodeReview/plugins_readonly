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
import com.google.gerrit.extensions.restapi.Response;
import com.google.gerrit.extensions.restapi.RestModifyView;
import com.google.gerrit.extensions.restapi.RestReadView;
import com.google.gerrit.server.config.ConfigResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;

public class ReadOnlyEndpoint {
  static class Input {}

  @RequiresAnyCapability({ADMINISTRATE_SERVER, MAINTAIN_SERVER})
  @Singleton
  public static class Get implements RestReadView<ConfigResource> {
    private final ReadOnlyState state;

    @Inject
    Get(ReadOnlyState state) {
      this.state = state;
    }

    @Override
    public Response<String> apply(ConfigResource resource) {
      return Response.ok(state.isReadOnly() ? "on" : "off");
    }
  }

  @RequiresAnyCapability({ADMINISTRATE_SERVER, MAINTAIN_SERVER})
  @Singleton
  public static class Put implements RestModifyView<ConfigResource, Input> {
    private final ReadOnlyState state;

    @Inject
    Put(ReadOnlyState state) {
      this.state = state;
    }

    @Override
    public Response<String> apply(ConfigResource resource, Input input) throws IOException {
      state.setReadOnly(true);
      return Response.ok("on");
    }
  }

  @RequiresAnyCapability({ADMINISTRATE_SERVER, MAINTAIN_SERVER})
  @Singleton
  public static class Delete implements RestModifyView<ConfigResource, Input> {
    private final ReadOnlyState state;

    @Inject
    Delete(ReadOnlyState state) {
      this.state = state;
    }

    @Override
    public Response<String> apply(ConfigResource resource, Input input) throws IOException {
      state.setReadOnly(false);
      return Response.ok("off");
    }
  }
}
