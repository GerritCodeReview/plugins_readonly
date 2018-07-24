load("//tools/bzl:plugin.bzl", "PLUGIN_DEPS")
load("//tools/bzl:classpath.bzl", "classpath_collector")

classpath_collector(
    name = "main_classpath_collect",
    testonly = 1,
    deps = [
        "//:readonly__plugin_test_deps",
    ],
)
