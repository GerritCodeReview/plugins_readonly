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

import static com.google.gerrit.sshd.CommandMetaData.Mode.MASTER_OR_SLAVE;

import com.google.gerrit.sshd.CommandMetaData;
import com.google.gerrit.sshd.SshCommand;
import com.google.inject.Inject;

@CommandMetaData(name = "disabled", description = "Disable ssh commands", runsAt = MASTER_OR_SLAVE)
class DisableCommand extends SshCommand {
  @Inject ReadOnlyConfig config;

  @Override
  protected void run() throws UnloggedFailure {
    throw new UnloggedFailure(1, "SSH subsystem is temporarily disabled: " + config.message());
  }
}
