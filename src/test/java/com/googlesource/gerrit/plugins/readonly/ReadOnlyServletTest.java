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

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gerrit.server.CurrentUser;
import com.google.gerrit.server.account.CapabilityControl;
import com.google.gerrit.server.config.SitePaths;
import com.google.inject.Provider;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReadOnlyServletTest {
  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  @Mock private Provider<CurrentUser> currentUserProviderMock;
  @Mock private CurrentUser currentUserMock;
  @Mock private CapabilityControl capabilityControlMock;

  private ReadOnlyServlet servlet;
  private SitePaths site;

  @Before
  public void setUp() throws Exception {
    when(currentUserProviderMock.get()).thenReturn(currentUserMock);
    when(currentUserMock.getCapabilities()).thenReturn(capabilityControlMock);
    when(capabilityControlMock.canAdministrateServer()).thenReturn(true);
    site = new SitePaths(tempFolder.getRoot().toPath());
    tempFolder.newFolder("etc");
    ReadOnlyState state = new ReadOnlyState(site);
    servlet = new ReadOnlyServlet(currentUserProviderMock, state);
  }

  @Test
  public void notReadOnlyByDefault() throws Exception {
    assertNotReadOnly();
  }

  @Test
  public void enableReadOnly() throws Exception {
    assertNotReadOnly();

    // enable read-only
    HttpServletResponse responseMock = mock(HttpServletResponse.class);
    servlet.doPost(null, responseMock);
    verify(responseMock).setStatus(SC_NO_CONTENT);
    assertIsReadOnly();

    // enabling read-only again should not change anything
    responseMock = mock(HttpServletResponse.class);
    servlet.doPost(null, responseMock);
    verify(responseMock).setStatus(SC_NO_CONTENT);
    assertIsReadOnly();
  }

  @Test
  public void enableReadOnlyByNonAdmins() throws Exception {
    assertNotReadOnly();

    when(capabilityControlMock.canAdministrateServer()).thenReturn(false);
    HttpServletResponse responseMock = mock(HttpServletResponse.class);
    servlet.doDelete(null, responseMock);
    verify(responseMock).sendError(SC_FORBIDDEN);
    assertNotReadOnly();
  }

  @Test
  public void errorDuringEnableReadOnly() throws Exception {
    // remove site dir to create an IOException
    tempFolder.delete();

    HttpServletResponse responseMock = mock(HttpServletResponse.class);
    servlet.doPost(null, responseMock);
    verify(responseMock).sendError(SC_INTERNAL_SERVER_ERROR);
  }

  @Test
  public void disableReadOnly() throws Exception {
    // first, mark as read-only
    servlet.doPost(null, mock(HttpServletResponse.class));
    assertIsReadOnly();

    // disable read-only
    HttpServletResponse responseMock = mock(HttpServletResponse.class);
    servlet.doDelete(null, responseMock);
    verify(responseMock).setStatus(SC_NO_CONTENT);
    assertNotReadOnly();

    // disabling read-only again should not change anything
    responseMock = mock(HttpServletResponse.class);
    servlet.doDelete(null, responseMock);
    verify(responseMock).setStatus(SC_NO_CONTENT);
    assertNotReadOnly();
  }

  @Test
  public void disableReadOnlyByNonAdmins() throws Exception {
    // first, mark as read-only
    servlet.doPost(null, mock(HttpServletResponse.class));
    assertIsReadOnly();

    when(capabilityControlMock.canAdministrateServer()).thenReturn(false);
    HttpServletResponse responseMock = mock(HttpServletResponse.class);
    servlet.doDelete(null, responseMock);
    verify(responseMock).sendError(SC_FORBIDDEN);
    assertIsReadOnly();
  }

  @Test
  public void errorDuringDisableReadOnly() throws Exception {
    // Create gerrit.readonly as a folder with content to create an IOException
    site.etc_dir.resolve("gerrit.readonly").resolve("child").toFile().mkdirs();

    HttpServletResponse responseMock = mock(HttpServletResponse.class);
    servlet.doDelete(null, responseMock);
    verify(responseMock).sendError(SC_INTERNAL_SERVER_ERROR);
  }

  @Test
  public void errorWhileSendingReadOnlyStatusResponse() throws IOException {
    HttpServletResponse responseMock = mock(HttpServletResponse.class);
    servlet.doPost(null, responseMock);
    verify(responseMock).setStatus(SC_NO_CONTENT);

    responseMock = mock(HttpServletResponse.class);
    doThrow(new IOException("someError")).when(responseMock).sendError(SC_SERVICE_UNAVAILABLE);
    servlet.doGet(null, responseMock);
    verify(responseMock).setStatus(SC_INTERNAL_SERVER_ERROR);
  }

  private void assertNotReadOnly() throws Exception {
    HttpServletResponse responseMock = mock(HttpServletResponse.class);
    servlet.doGet(null, responseMock);
    verify(responseMock).setStatus(SC_NO_CONTENT);
  }

  private void assertIsReadOnly() throws Exception {
    HttpServletResponse responseMock = mock(HttpServletResponse.class);
    servlet.doGet(null, responseMock);
    verify(responseMock).sendError(SC_SERVICE_UNAVAILABLE);
  }
}
