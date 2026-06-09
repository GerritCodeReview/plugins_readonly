load(
    "@com_googlesource_gerrit_bazlets//:gerrit_plugin.bzl",
    "gerrit_plugin",
    "gerrit_plugin_tests",
)

PLUGIN = "readonly"

gerrit_plugin(
    name = PLUGIN,
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: readonly",
        "Gerrit-Module: com.googlesource.gerrit.plugins.readonly.Module",
        "Gerrit-SshModule: com.googlesource.gerrit.plugins.readonly.SshModule",
        "Gerrit-HttpModule: com.googlesource.gerrit.plugins.readonly.HttpModule",
    ],
    resources = glob(["src/main/resources/**/*"]),
)

gerrit_plugin_tests(
    name = "readonly_tests",
    srcs = glob(["src/test/java/**/*.java"]),
    plugin = PLUGIN,
    resources = glob(["src/test/resources/**/*"]),
)
