"""
Setup script for AIKA Python package.

This makes the Python helper modules (in the python/ directory) installable,
allowing clean imports without sys.path manipulation.

Usage:
    pip install -e .  # Install in development mode
"""

from setuptools import setup, find_packages

setup(
    name="aika-python-helpers",
    version="1.0.0",
    description="Python helper modules for AIKA neural network framework",
    author="AIKA Project",
    python_requires=">=3.8",
    packages=find_packages(where="."),
    package_dir={"": "."},
    install_requires=[
        "parameterized",
        "pybind11>=2.10.0",  # Required for C++ bindings
    ],
    extras_require={
        "dev": [
            "pytest",
            "pytest-cov",
        ],
    },
    # Don't include the C++ extension here - it's built and installed via CMake
    # This setup.py is only for the Python helper modules
)
