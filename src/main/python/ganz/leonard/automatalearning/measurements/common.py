import os

KEY_STABILITIES = 'stability'
KEY_AVG_STABILITY = 'avgStability'

FILENAME_STATS = "stats.json"
NORMALIZED_Y_LIMIT = [0, 1.1]


def get_measurements_dir():
    script_dir = os.path.dirname(__file__)
    project_dir = os.path.abspath(os.path.join(script_dir, "../../../../../../.."))
    return os.path.join(project_dir, "measurements")


def contains_dirs(path):
    if os.path.isfile(path):
        return False
    for inner_entry in os.scandir(path):
        if os.path.isdir(inner_entry):
            return True
    return False
