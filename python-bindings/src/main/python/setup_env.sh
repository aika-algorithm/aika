#!/bin/sh

# Create the virtual environment
python3 -m venv target/venv

# Activate the virtual environment
. target/venv/bin/activate

python3 --version

target/venv/bin/pip install setuptools

# Install the required packages
target/venv/bin/pip install -r src/main/python/requirements.txt