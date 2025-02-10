cmake_minimum_required(VERSION 3.30)
project(aika)

# Find Python and pybind11
find_package(Python REQUIRED)
find_package(pybind11 REQUIRED)

set(CMAKE_CXX_STANDARD 20)

# Add source files
set(SOURCES
        src/fields/field.cpp
        src/fields/field_definition.cpp
        src/fields/field_link_definition.cpp
        src/fields/type.cpp
        src/fields/type_registry.cpp
        src/fields/type_registry_python.cpp
        src/fields/flattened_type.cpp
        src/fields/flattened_type_relation.cpp
        src/fields/direction.cpp
        src/network/model.cpp
)

include_directories(${PROJECT_SOURCE_DIR}/include)
message(STATUS "Project Source Dir: ${PROJECT_SOURCE_DIR}")

pybind11_add_module(aika ${SOURCES})

# EXAMPLE_VERSION_INFO is defined by setup.py and passed into the C++ code as a
# define (VERSION_INFO) here.
target_compile_definitions(aika
                           PRIVATE VERSION_INFO=${EXAMPLE_VERSION_INFO})

# Link pybind11 and Python libraries
target_link_libraries(aika PRIVATE pybind11::module)


# Install the module
install(TARGETS aika DESTINATION ${PYTHON_SITE_PACKAGES}/aika)

# Optionally, install the include directories if you need to build it later
install(DIRECTORY include/ DESTINATION ${PYTHON_SITE_PACKAGES}/aika/include)
