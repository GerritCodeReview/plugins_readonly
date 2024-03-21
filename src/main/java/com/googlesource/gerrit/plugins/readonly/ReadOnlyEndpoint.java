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

import static com.google.gerrit.server.permissions.GlobalPermission.ADMINISTRATE_SERVER;
import static com.google.gerrit.server.permissions.GlobalPermission.MAINTAIN_SERVER;

import com.google.gerrit.extensions.restapi.AuthException;
import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.permissions.PermissionBackend;
import com.google.gerrit.server.permissions.PermissionBackendException;
import com.google.inject.Provider;
import java.util.Set;

public class ReadOnlyEndpoint {
  private final Provider<CurrentUser> userProvider;
  private final PermissionBackend permissionBackend;

  public ReadOnlyEndpoint(Provider<CurrentUser> userProvider, PermissionBackend permissionBackend) {
    this.userProvider = userProvider;
    this.permissionBackend = permissionBackend;
  }

  void checkPermissions() throws AuthException, PermissionBackendException {
    CurrentUser requestingUser = userProvider.get();
    if (requestingUser == null || !requestingUser.isIdentifiedUser()) {
      throw new AuthException("authentication required");
    }

    permissionBackend.user(requestingUser).checkAny(Set.of(ADMINISTRATE_SERVER, MAINTAIN_SERVER));
  }
}
