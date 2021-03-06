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
import static com.google.gerrit.testing.GerritJUnit.assertThrows;
import static org.apache.http.HttpStatus.SC_SERVICE_UNAVAILABLE;

import com.google.gerrit.acceptance.GitUtil;
import com.google.gerrit.acceptance.LightweightPluginDaemonTest;
import com.google.gerrit.acceptance.TestPlugin;
import com.google.gerrit.acceptance.UseLocalDisk;
import com.google.gerrit.acceptance.UseSsh;
import com.google.gerrit.extensions.api.changes.TopicInput;
import com.google.gerrit.extensions.common.ChangeInfo;
import com.google.gerrit.extensions.common.ChangeInput;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
@TestPlugin(
    name = "readonly",
    sysModule = "com.googlesource.gerrit.plugins.readonly.Module",
    httpModule = "com.googlesource.gerrit.plugins.readonly.HttpModule",
    sshModule = "com.googlesource.gerrit.plugins.readonly.SshModule")
public abstract class AbstractReadOnlyTest extends LightweightPluginDaemonTest {
  @Test
  @UseLocalDisk
  public void restRequestsAreRejectedWhenReadOnly() throws Exception {
    ChangeInput in = new ChangeInput();
    in.project = project.get();
    in.branch = "master";
    in.subject = "test";
    ChangeInfo change = gApi.changes().create(in).get();

    // GET should be allowed
    String url = "/changes/" + change.changeId;
    adminRestSession.get(url).assertOK();

    // PUT should be allowed
    TopicInput topic = new TopicInput();
    topic.topic = "topic";
    adminRestSession.put(url + "/topic", topic).assertOK();

    // DELETE should be allowed
    adminRestSession.delete(url + "/topic").assertNoContent();

    // POST should be allowed
    adminRestSession.post(url + "/abandon").assertOK();

    // Enable read-only
    setReadOnly(true);

    // GET should be allowed
    adminRestSession.get(url).assertOK();

    // PUT should be blocked
    adminRestSession.put(url + "/topic", topic).assertStatus(SC_SERVICE_UNAVAILABLE);

    // DELETE should be blocked
    adminRestSession.delete(url + "/topic").assertStatus(SC_SERVICE_UNAVAILABLE);

    // POST should be blocked
    adminRestSession.post(url + "/restore").assertStatus(SC_SERVICE_UNAVAILABLE);

    // Disable read-only
    setReadOnly(false);

    // GET should be allowed
    adminRestSession.get(url).assertOK();

    // PUT should be allowed
    adminRestSession.put(url + "/topic", topic).assertOK();

    // DELETE should be allowed
    adminRestSession.delete(url + "/topic").assertNoContent();

    // POST should be allowed
    adminRestSession.post(url + "/restore").assertOK();
  }

  @Test
  @UseLocalDisk
  @UseSsh
  public void sshCommandsAreRejectedWhenReadOnly() throws Exception {
    String command = "gerrit ls-projects";

    // Command should succeed
    adminSshSession.exec(command);
    adminSshSession.assertSuccess();

    // Enable read-only
    setReadOnly(true);

    // Command should be blocked
    adminSshSession.exec(command);
    adminSshSession.assertFailure("READ ONLY");

    // Disable read-only
    setReadOnly(false);

    // Command should succeed
    adminSshSession.exec(command);
    adminSshSession.assertSuccess();
  }

  @Test
  @UseLocalDisk
  @UseSsh
  public void pushBySshIsRejectedWhenReadOnly() throws Exception {
    pushForReview(true, "READ ONLY");
  }

  @Test
  @UseLocalDisk
  public void pushByHttpIsRejectedWhenReadOnly() throws Exception {
    pushForReview(false, "Service Unavailable");
  }

  private void pushForReview(boolean ssh, String expectedMessage) throws Exception {
    String url = ssh ? adminSshSession.getUrl() : admin.getHttpUrl(server);
    if (!ssh) {
      CredentialsProvider.setDefault(
          new UsernamePasswordCredentialsProvider(admin.username(), admin.httpPassword()));
    }
    testRepo = GitUtil.cloneProject(project, url + "/" + project.get());

    // Push should succeed
    pushTo("refs/for/master").assertOkStatus();

    // Enable read-only
    setReadOnly(true);

    // Push should fail
    TransportException thrown =
        assertThrows(TransportException.class, () -> pushTo("refs/for/master"));
    assertThat(thrown).hasMessageThat().contains(expectedMessage);

    // Disable read-only
    setReadOnly(false);

    // Push should succeed
    pushTo("refs/for/master").assertOkStatus();
  }

  protected abstract void setReadOnly(boolean readOnly) throws Exception;
}
