cmake_minimum_required(VERSION 3.30)
project(aika)

set(CMAKE_CXX_STANDARD 20)

# Add source files
set(SOURCES
        src/fields/field.cpp
        src/fields/field_definition.cpp
        src/fields/type.cpp
        src/fields/type_registry.cpp
        src/fields/type_registry_python.cpp
        src/network/model.cpp
)

include_directories(${PROJECT_SOURCE_DIR}/include)
message(STATUS "Project Source Dir: ${PROJECT_SOURCE_DIR}")

add_subdirectory(pybind11)
pybind11_add_module(aika ${SOURCES})

# EXAMPLE_VERSION_INFO is defined by setup.py and passed into the C++ code as a
# define (VERSION_INFO) here.
target_compile_definitions(aika
                           PRIVATE VERSION_INFO=${EXAMPLE_VERSION_INFO})
