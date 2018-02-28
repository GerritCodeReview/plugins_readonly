
package com.googlesource.gerrit.plugins.readonly;

import com.google.gerrit.extensions.registration.DynamicItem;
import com.google.gerrit.sshd.PluginCommandModule;
import com.google.gerrit.sshd.SshCreateCommandInterceptor;

public class SshModule extends PluginCommandModule {
  @Override
  protected void configureCommands() {
    DynamicItem.bind(binder(), SshCreateCommandInterceptor.class)
        .to(HijackCommandInterceptor.class);
    command(HijackCommand.class);
  }
}
