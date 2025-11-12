# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Prerequisites

- Python 3.8+ with pip
- CMake 3.30+
- C++ compiler with C++20 support
- pybind11 (installed via `pip install -e .` or system package manager)

## Build & Test Commands

### Recommended: Out-of-Source Build
```bash
# Initial setup
mkdir -p build && cd build
cmake ..
cmake --build . --target install

# Clean rebuild
cd build
rm -rf *
cmake ..
cmake --build . --target install
```

### Alternative: In-Source Build (Legacy)
```bash
# Clean & Build
make clean && cmake --build . --target install

# CMake Configure
cmake .  # (run after changes to CMakeLists.txt)
```

### Testing
- **Run All Python Tests**: `python -m unittest discover tests/python`
- **Run Single Python Test**: `python tests/python/[test-file].py` (e.g., `python tests/python/addition-test.py`)
- **Run C++ Tests**: `cd build && ctest` or `./build/activation_tests`
- **Test Directory Structure**: All tests are organized under `tests/` with `tests/cpp/` and `tests/python/` subdirectories

### Build Configuration
- **Build for Debug**: Set `CMAKE_BUILD_TYPE=Debug` in CMakeLists.txt (currently default)
- **Build for Release**: Set `CMAKE_BUILD_TYPE=Release` in CMakeLists.txt

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