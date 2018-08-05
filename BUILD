load(
    "//tools/bzl:plugin.bzl",
    "PLUGIN_DEPS",
    "PLUGIN_TEST_DEPS",
    "gerrit_plugin",
)
load("//tools/bzl:junit.bzl", "junit_tests")

gerrit_plugin(
    name = "readonly",
    srcs = glob(["src/main/java/**/*.java"]),
    manifest_entries = [
        "Gerrit-PluginName: readonly",
        "Gerrit-Module: com.googlesource.gerrit.plugins.readonly.Module",
        "Gerrit-SshModule: com.googlesource.gerrit.plugins.readonly.SshModule",
        "Gerrit-HttpModule: com.googlesource.gerrit.plugins.readonly.HttpModule",
    ],
    resources = glob(["src/main/resources/**/*"]),
)

junit_tests(
    name = "readonly_tests",
    srcs = glob(["src/test/java/**/*.java"]),
    resources = glob(["src/test/resources/**/*"]),
    deps = [
        ":readonly__plugin_test_deps",
    ],
)

java_library(
    name = "readonly__plugin_test_deps",
    testonly = 1,
    visibility = ["//visibility:public"],
    exports = PLUGIN_DEPS + PLUGIN_TEST_DEPS + [
        ":readonly__plugin",
        "@mockito//jar",
    ],
)
