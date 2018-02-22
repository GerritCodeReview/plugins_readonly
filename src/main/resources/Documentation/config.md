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
