cmake_minimum_required(VERSION 3.12)
project(PybindExample)

# Find pybind11
find_package(pybind11 REQUIRED)

# Define the C++ extension module
pybind11_add_module(example example.cpp)

# Find Python and link it
find_package(Python3 REQUIRED COMPONENTS Interpreter Development)

target_link_libraries(example PRIVATE Python3::Python)
