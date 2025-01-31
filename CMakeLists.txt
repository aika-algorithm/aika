cmake_minimum_required(VERSION 3.30)
project(aika)

set(CMAKE_CXX_STANDARD 20)

# Add source files
set(SOURCES
        src/main.cpp
        src/fields/TypeRegistry.cpp
)

add_subdirectory(pybind11)
pybind11_add_module(aika ${SOURCES})

# EXAMPLE_VERSION_INFO is defined by setup.py and passed into the C++ code as a
# define (VERSION_INFO) here.
target_compile_definitions(aika
                           PRIVATE VERSION_INFO=${EXAMPLE_VERSION_INFO})
