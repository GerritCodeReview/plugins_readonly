@PLUGIN@ Configuration
======================

The @PLUGIN@ plugin is configured in the `$site_path/etc/@PLUGIN@.config` file:

File '@PLUGIN@.config'
----------------------

```
[readonly]
  message = Gerrit is down for maintenance
```

If the configuration is modified, the plugin must be reloaded for the changes to
be effective.


```readonly.message```
:   Message to be shown to clients when attempting to perform an opeation that
    is blocked due to the server being in read-only mode. When not specified,
    the default is "Gerrit is under maintenance - all data is READ ONLY".

```readonly.markerDir```
:   Directory in which to create the file marking that Gerrit is in readonly mode.
    By default, this is `$SITE/etc`.

```readonly.allowSshCommand```
:   Allow one or more SSH commands to be executed. When the allow value starts
    with a caret '^' then it is interpreted as regex, otherwise as a prefix.
    Repeat with multiple values to allow more than one command or pattern
    of commands.
    The command `gerrit plugin rm` or `gerrit plugin remove` is always allowed.
    The following read-only commands are always allowed:
    - `gerrit apropos`
    - `gerrit check-project-access`
    - `gerrit logging ls-level`
    - `gerrit ls-groups`
    - `gerrit ls-members`
    - `gerrit ls-projects`
    - `gerrit ls-user-refs`
    - `gerrit plugin ls`
    - `gerrit query`
    - `gerrit sequence show`
    - `gerrit show-caches`
    - `gerrit show-connections`
    - `gerrit show-queue`
    - `gerrit stream-events`, this allows you to keep the stream-events SSH connection
        open while the server is read-only. Though while the server is read-only it will
        not send any events.
    - `gerrit version`
    - `git upload-pack`
    - `scp`


File gerrit.readonly
--------------------

Marker file used to enable or disable the read-only status. When present under
the $GERRIT_SITE/etc directory, Gerrit is set to read-only and only read
operations are enabled according to the readonly plugin configuration.
