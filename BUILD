load("//tools/bzl:plugin.bzl", "gerrit_plugin")

gerrit_plugin(
    name = "readonly",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: readonly",
        "Gerrit-Module: com.googlesource.gerrit.plugins.readonly.Module",
    ],
    resources = glob(["src/main/**/*"]),
)
