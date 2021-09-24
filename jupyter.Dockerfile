# Pick your favorite docker-stacks image
FROM jupyter/minimal-notebook:python-3.8.8
USER root

# Add permanent apt-get installs and other root commands here
# e.g., RUN apt-get install --yes --no-install-recommends npm nodejs

USER jovyan

# Switch back to jovyan to avoid accidental container runs as root
# Add permanent mamba/pip/conda installs, data files, other user libs here
# e.g., RUN pip install --quiet --no-cache-dir flake8
RUN pip install kafka-python requests sseclient numpy matplotlib ipympl