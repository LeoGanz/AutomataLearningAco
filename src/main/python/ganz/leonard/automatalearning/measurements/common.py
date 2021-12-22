import os


def get_measurements_dir():
    script_dir = os.path.dirname(__file__)
    project_dir = os.path.abspath(os.path.join(script_dir, "../../../../../../.."))
    return os.path.join(project_dir, "measurements")


def contains_dirs(path):
    for inner_entry in os.scandir(path):
        if os.path.isdir(inner_entry):
            return True
    return False
