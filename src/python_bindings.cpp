#include <pybind11/pybind11.h>

#include "fields/field_bindings.h"
#include "network/network_bindings.h"


// ----------------
// Python interface
// ----------------

namespace py = pybind11;

PYBIND11_MODULE(aika, m)
{
    auto m_fields = m.def_submodule("fields", "Fields module");
    bind_fields(m_fields);

    auto m_network = m.def_submodule("network", "Network module");
    bind_network(m_network);
}