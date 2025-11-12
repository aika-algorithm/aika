"""
Python package for AIKA neural network implementations.

This package provides high-level abstractions and utilities for building
neural networks using the AIKA C++ core.

Modules:
    networks: Network architecture implementations (standard, transformer)
    types: Type definitions for neural components (softmax, dot product)
    utils: Debugging and utility functions
    examples: Example implementations and demonstrations
"""

from . import networks
from . import types
from . import utils
from . import examples

__all__ = [
    'networks',
    'types',
    'utils',
    'examples',
]

__version__ = '1.0.0'