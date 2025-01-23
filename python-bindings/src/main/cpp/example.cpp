#include <pybind11/pybind11.h>
#include <pybind11/stl.h>
#include <vector>
#include <iostream>
#include <dlfcn.h> // For dlopen and dlsym
#include <hello-world.h>



// ----------------
// Regular C++ code
// ----------------

// multiply all entries by 2.0
// input:  std::vector ([...]) (read only)
// output: std::vector ([...]) (new copy)
std::vector<double> modify(const std::vector<double>& input)
{
  std::vector<double> output;

  std::transform(
    input.begin(),
    input.end(),
    std::back_inserter(output),
    [](double x) -> double { return 2.*x; }
  );

  // N.B. this is equivalent to (but there are also other ways to do the same)
  //
  // std::vector<double> output(input.size());
  //
  // for ( size_t i = 0 ; i < input.size() ; ++i )
  //   output[i] = 2. * input[i];

  return output;
}

// ----------------
// Python interface
// ----------------

namespace py = pybind11;

// Load and call the GraalVM native library
const char* call_graalvm_method(char* input)
{
    graal_isolate_t *isolate = NULL;
    graal_isolatethread_t *thread = NULL;

    if (graal_create_isolate(NULL, &isolate, &thread) != 0) {
        return "initialization error\n";
    }

    // Call the function
    const char* result = greet(thread, input);

    // Duplicate the string to ensure the memory is managed independently
    size_t len = strlen(result);
    char* duplicated_result = new char[len + 1];
    strncpy(duplicated_result, result, len);
    duplicated_result[len] = '\0';  // Ensure null termination

    graal_tear_down_isolate(thread);

    return duplicated_result;
}


PYBIND11_MODULE(example,m)
{
  m.doc() = "pybind11 example plugin";

  m.def("modify", &modify, "Multiply all entries of a list by 2.0");

  // GraalVM native function exposed to Python
  m.def("call_graalvm", &call_graalvm_method, "Call a GraalVM native method");
}

int main() {
    // Example input string
    char input[] = "World";

    // Call the GraalVM native method
    const char* result = call_graalvm_method(input);

    // Check if the result is valid and print the result
    if (result) {
        std::cout << "GraalVM result: " << result << std::endl;
    } else {
        std::cerr << "Error calling GraalVM method." << std::endl;
    }

    return 0;
}