cmake_minimum_required(VERSION 3.22.1)
project(ogs_android LANGUAGES CXX)

add_library(ogs_android SHARED android_bridge.cpp)

find_library(android-lib android)
find_library(log-lib log)

target_compile_features(ogs_android PRIVATE cxx_std_17)
target_compile_options(ogs_android PRIVATE -Wall -Wextra -Werror=return-type)
target_link_libraries(ogs_android PRIVATE ${android-lib} ${log-lib})
