# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

- **Clean & Build**: `make clean && cmake --build . --target install`
- **Run All Tests**: `python -m unittest discover python-tests`
- **Run Single Test**: `python python-tests/[test-file].py` (e.g., `python python-tests/addition-test.py`)
- **Build for Debug**: Set `CMAKE_BUILD_TYPE=Debug` in CMakeLists.txt
- **CMake Configure**: `cmake .` (run after changes to CMakeLists.txt)

## Code Style Guidelines

### C++ Style
- **Headers**: Use include guards (`#ifndef FILE_H`, `#define FILE_H`, `#endif`)
- **Documentation**: Doxygen-style comments for classes, methods, and parameters
- **Naming**: 
  - Classes: PascalCase (e.g., `FieldDefinition`)
  - Methods: camelCase (e.g., `setValue`, `getObject`)
  - Variables: camelCase (e.g., `fieldDefinition`, `withinUpdate`)
- **Memory Management**: Prefer smart pointers where appropriate
- **Error Handling**: Use specific exception classes (e.g., `LockException`, `MissingNeuronException`)

### Python Style
- **Imports**: Standard library first, third-party packages next, local modules last
- **Testing**: Use unittest framework with descriptive test case names
- **Type Registry**: Always call `flattenTypeHierarchy()` after setting up type relations
- **Object Instantiation**: Use `instantiate()` method on types, link objects explicitly