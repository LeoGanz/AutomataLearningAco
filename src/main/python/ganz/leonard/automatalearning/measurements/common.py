import os
from collections import Counter

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


def scatter_multival(X, Y, labels, ax, force_large_font):
    if isinstance(Y[0], list) or isinstance(Y[0], tuple):
        all_points = []
        for x in range(len(X)):
            for iteration in range(len(Y[0])):
                all_points.append((x, Y[x][iteration]))
        ctr = Counter(all_points)

        for iteration in range(len(Y[0])):
            Y_ = []  # collect ys for one iteration
            for x in range(len(X)):
                Y_.append(Y[x][iteration])
            sizes = [20 * ctr[point] for point in zip(X, Y_)]
            ax.scatter(X, Y_, alpha=0.7, s=sizes, label=labels[iteration])
        # https://stackoverflow.com/a/45220580
        ax.plot([], [], ' ', label="Size => # of points")
    else:
        ax.scatter(X, Y, label=labels)
    lgnd = ax.legend(loc="lower left", fontsize=7 if not force_large_font and len(labels) > 6 else 10)
    # https://stackoverflow.com/a/43578952
    for handle in lgnd.legendHandles:
        if hasattr(handle, "set_sizes"):
            handle.set_sizes([40.0])
