// Copyright (C) 2024 The Android Open Source Project
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

import com.google.gerrit.extensions.restapi.AuthException;
import com.google.gerrit.extensions.restapi.Response;
import com.google.gerrit.extensions.restapi.RestReadView;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.config.ConfigResource;
import com.google.gerrit.server.permissions.PermissionBackend;
import com.google.gerrit.server.permissions.PermissionBackendException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class GetReadOnly extends ReadOnlyEndpoint implements RestReadView<ConfigResource> {
  private final ReadOnlyState state;

  @Inject
  public GetReadOnly(
      Provider<CurrentUser> userProvider,
      PermissionBackend permissionBackend,
      ReadOnlyState state) {
    super(userProvider, permissionBackend);
    this.state = state;
  }

  @Override
  public Response<String> apply(ConfigResource resource)
      throws AuthException, PermissionBackendException {
    checkPermissions();
    return Response.ok(state.isReadOnly() ? "on" : "off");
  }
}
