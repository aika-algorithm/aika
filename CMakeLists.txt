cmake_minimum_required(VERSION 3.30)
project(aika)

set(PYTHON_SITE_PACKAGES /Users/lukasmolzberger/CLionProjects/aika-cpp/.venv/lib/python3.12/site-packages)
set(CMAKE_OSX_SYSROOT /Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX15.2.sdk)
set(CMAKE_CXX_STANDARD 20)

# Find Python and pybind11
find_package(Python REQUIRED COMPONENTS Interpreter Development REQUIRED)
find_package(pybind11 REQUIRED)


# Add source files
set(SOURCES
        src/fields/field.cpp
        src/fields/field_definition.cpp
        src/fields/abstract_function_definition.cpp
        src/fields/input_field.cpp
        src/fields/addition.cpp
        src/fields/subtraction.cpp
        src/fields/multiplication.cpp
        src/fields/division.cpp
        src/fields/exponential_function.cpp
        src/fields/summation.cpp
        src/fields/field_link_definition.cpp
        src/fields/obj.cpp
        src/fields/type.cpp
        src/fields/type_registry.cpp
        src/fields/flattened_type.cpp
        src/fields/flattened_type_relation.cpp
        src/fields/relation.cpp
        src/fields/direction.cpp
        src/fields/queue_interceptor.cpp
        src/fields/field_update.cpp
        src/fields/utils.cpp
        src/fields/queue.cpp
        src/fields/queue_key.cpp
        src/fields/step.cpp
        src/fields/test_type.cpp
        src/fields/test_object.cpp
        src/fields/python_bindings.cpp
        src/network/model.cpp
)

include_directories(${PROJECT_SOURCE_DIR}/include)
message(STATUS "Project Source Dir: ${PROJECT_SOURCE_DIR}")

# Add these lines before pybind11_add_module
set(CMAKE_POSITION_INDEPENDENT_CODE ON)
set(CMAKE_CXX_VISIBILITY_PRESET hidden)
set(CMAKE_VISIBILITY_INLINES_HIDDEN ON)
set(CMAKE_BUILD_TYPE Debug)

pybind11_add_module(aika ${SOURCES})

# EXAMPLE_VERSION_INFO is defined by setup.py and passed into the C++ code as a
# define (VERSION_INFO) here.
target_compile_definitions(aika
                           PRIVATE VERSION_INFO=${EXAMPLE_VERSION_INFO}
                                   PYBIND11_DETAILED_ERROR_MESSAGES)

# Add these compilation flags
target_compile_options(aika PRIVATE
    $<$<CXX_COMPILER_ID:GNU,Clang,AppleClang>:-frtti>
)

# Link pybind11 and Python libraries
target_link_libraries(aika PRIVATE pybind11::module)


# Install the module
install(TARGETS aika DESTINATION ${PYTHON_SITE_PACKAGES}/aika)

# Optionally, install the include directories if you need to build it later
install(DIRECTORY include/ DESTINATION ${PYTHON_SITE_PACKAGES}/aika/include)
