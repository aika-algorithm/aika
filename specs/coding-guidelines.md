
# General Coding Guidelines
- **Setup**: C++ library using CMake as the build system and pybind11 for Python integration (similar to PyTorch).
  The C++ code is intended to be high performance with as little overhead as possible. The code base should avoid smart pointers and manage its memory manully.
  For perfomance critical portions of the code, dynamic data structures should be avoided and array based data-structure should be used instead.
