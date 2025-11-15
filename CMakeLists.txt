cmake_minimum_required(VERSION 3.30)
project(aika)

# Prevent in-source builds
if(CMAKE_SOURCE_DIR STREQUAL CMAKE_BINARY_DIR)
    message(WARNING "In-source builds are discouraged. Please use: mkdir build && cd build && cmake ..")
endif()

set(CMAKE_OSX_SYSROOT /Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX15.2.sdk)
set(CMAKE_CXX_STANDARD 20)

# Find Python and pybind11
find_package(Python REQUIRED COMPONENTS Interpreter Development REQUIRED)
find_package(pybind11 REQUIRED)

# Determine Python site-packages directory
# Allow override via environment variable or use detected Python path
if(DEFINED ENV{PYTHON_SITE_PACKAGES})
    set(PYTHON_SITE_PACKAGES $ENV{PYTHON_SITE_PACKAGES})
elseif(DEFINED Python_SITELIB)
    set(PYTHON_SITE_PACKAGES ${Python_SITELIB})
else()
    # Fallback to .venv in project root
    set(PYTHON_SITE_PACKAGES ${CMAKE_SOURCE_DIR}/.venv/lib/python${Python_VERSION_MAJOR}.${Python_VERSION_MINOR}/site-packages)
endif()
message(STATUS "Python site-packages: ${PYTHON_SITE_PACKAGES}")


# Add source files
set(SOURCES
        src/python_bindings.cpp
        src/fields/field_bindings.cpp
        src/network/network_bindings.cpp
        ${CORE_SOURCES}
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

# Link pybind11 and Python libraries, and the core library
target_link_libraries(aika PRIVATE pybind11::module aika_core)


# Install the module directly to site-packages (not in a subdirectory)
install(TARGETS aika DESTINATION ${PYTHON_SITE_PACKAGES})

# Optionally, install the include directories if you need to build it later
# Note: Headers are typically not needed at runtime, consider removing this if not needed
install(DIRECTORY include/ DESTINATION ${PYTHON_SITE_PACKAGES}/aika_include)

# Enable testing
enable_testing()

# Create a core library without Python bindings for testing
set(CORE_SOURCES
        src/fields/field.cpp
        src/fields/field_definition.cpp
        src/fields/abstract_function_definition.cpp
        src/fields/input_field.cpp
        src/fields/identity_field.cpp
        src/fields/proxy_field.cpp
        src/fields/addition.cpp
        src/fields/subtraction.cpp
        src/fields/multiplication.cpp
        src/fields/division.cpp
        src/fields/exponential_function.cpp
        src/fields/summation.cpp
        src/fields/field_activation_function.cpp
        src/fields/activation_functions.cpp
        src/fields/field_link_definition.cpp
        src/fields/object.cpp
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
        src/network/model.cpp
        src/network/activation.cpp
        src/network/activation_key.cpp
        src/network/activations_per_context.cpp
        src/network/binding_signal.cpp
        src/network/config.cpp
        src/network/direction.cpp
        src/network/context.cpp
        src/network/element.cpp
        src/network/element_step.cpp
        src/network/f_s_suspension_callback.cpp
        src/network/fired.cpp
        src/network/fired_queue_key.cpp
        src/network/in_memory_suspension_callback.cpp
        src/network/input.cpp
        src/network/link.cpp
        src/network/lock_exception.cpp
        src/network/missing_neuron_exception.cpp
        src/network/model_provider.cpp
        src/network/neuron.cpp
        src/network/neuron_reference.cpp
        src/network/neuron_serialization_exception.cpp
        src/network/output.cpp
        src/network/phase.cpp
        src/network/read_write_lock.cpp
        src/network/ref_type.cpp
        src/network/save.cpp
        src/network/suspension_callback.cpp
        src/network/synapse.cpp
        src/network/transition.cpp
        src/network/utils.cpp
        src/network/linker.cpp
        src/network/types/neuron_type.cpp
        src/network/types/activation_type.cpp
        src/network/types/synapse_type.cpp
        src/network/types/link_type.cpp
        src/network/builders/neuron_type_builder.cpp
        src/network/builders/synapse_type_builder.cpp
)

# Create core library
add_library(aika_core STATIC ${CORE_SOURCES})
target_include_directories(aika_core PUBLIC include)
target_compile_features(aika_core PUBLIC cxx_std_20)

# Add activation tests
add_executable(activation_tests
    tests/cpp/network/test_runner.cpp
    tests/cpp/network/activation_test.cpp
    tests/cpp/network/abstract_activation_test.cpp
    tests/cpp/network/link_latent_test.cpp
)

target_link_libraries(activation_tests PRIVATE aika_core)
target_include_directories(activation_tests PRIVATE include)

# Register tests with CTest
add_test(NAME ActivationTests COMMAND activation_tests)
add_test(NAME SimpleActivationTests COMMAND simple_activation_tests)
