"""
Test configuration module that handles Python path setup for all tests.
Import this at the top of test files instead of manual sys.path manipulation.
"""

import sys
import os

def setup_project_path():
    """Add project root to Python path if not already present."""
    project_root = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
    if project_root not in sys.path:
        sys.path.insert(0, project_root)

# Auto-setup when imported
setup_project_path()