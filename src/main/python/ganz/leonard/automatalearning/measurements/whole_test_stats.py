import os
import json
import csv
import numbers
import matplotlib.pyplot as plt
from common import *
import numpy as np


def stats_for_whole_test(path_to_test, property, plot_as_discrete_values):
    test_name = path_to_test.split('\\')[-1]
    is_single_value_property = False
    low_log = []
    x_label_global = ""
    x_ticks = []
    X = []
    Y = []
    Y2 = []

    for subtest in os.scandir(path_to_test):
        if os.path.isfile(subtest):
            continue
        stats_path = os.path.join(subtest, FILENAME_STATS)
        path_segments = stats_path.split('\\')
        subtest_name = path_segments[-2]
        print(subtest_name)
        subtest_name_segments = subtest_name.split(' ')

        # Extract label and x tick info from path
        x_label = ""
        x_tick = ""
        for name_elem in subtest_name_segments:
            if name_elem.startswith('subtest'):
                X.append(int(name_elem[name_elem.rindex('-') + 1:]))
            else:
                if len(x_label_global) <= 0:
                    if len(x_label) > 0:
                        x_label += ';'
                    x_label += name_elem[:name_elem.rindex('_')]
                if len(x_tick) > 0:
                    x_tick += ';'
                x_tick += name_elem[name_elem.rindex('_') + 1:]
        if len(x_label_global) <= 0:
            x_label_global = x_label
        x_ticks.append(x_tick)

        # Extract values from stats file
        with open(stats_path) as f:
            subtest_stats = json.load(f)
            if property not in subtest_stats:
                return
            is_single_value_property = isinstance(subtest_stats[property], numbers.Number)
            if is_single_value_property and subtest_stats[property] < 1:
                low_log.append((X[-1], subtest_stats[property]))
            Y.append(subtest_stats[property])
            Y2.append(subtest_stats[KEY_AVG_STABILITY])

    # Plot property
    X, Y, Y2, x_ticks = (list(t) for t in zip(*sorted(zip(X, Y, Y2, x_ticks))))
    fig, ax1 = plt.subplots()
    ax1.set_title(test_name)
    ax1.set_ylabel(property + " in (matched words / total words)")
    ax1.set_xlabel(x_label_global)
    every_nth = max(1, len(X) // 10)
    for i in range(len(x_ticks)):
        if i % every_nth != 0:
            x_ticks[i] = None
    plt.xticks(X, x_ticks)
    ax1.set_ylim(NORMALIZED_Y_LIMIT)
    color = plt.cm.viridis(np.linspace(0, 1, 1 if is_single_value_property else len(Y[0])))
    ax1.set_prop_cycle(plt.cycler("color", color))
    if is_single_value_property:
        labels = property
    else:
        labels = [f"Best score in iter. {x}" for x in range(len(Y[0]))]

    if plot_as_discrete_values:
        scatter_multival(X, Y, labels, ax1, is_single_value_property)
    else:  # no discrete values
        ax1.plot(X, Y, label=labels)
        ax1.legend(loc="lower left")

    # Plot stability
    ax2 = ax1.twinx()
    ax2.set_ylim(NORMALIZED_Y_LIMIT)
    ax2.set_ylabel("Average Stability")
    if plot_as_discrete_values:
        ax2.scatter(X, Y2, color="red", marker='x', label="Stability")
    else:
        ax2.plot(X, Y2, color="red", label="Stability")
    ax2.legend(loc="lower right")

    # Finalize plot
    plt.show()
    suffix = "-scatter" if plot_as_discrete_values else "-line"
    plot_path = os.path.join(path_to_test, f"stats_{property}{suffix}.png")
    print("writing plot to: " + plot_path)
    fig.savefig(plot_path)

    if is_single_value_property:
        write_low_log(low_log, path_to_test, property)


def write_low_log(low_log, path_to_test, property):
    low_log = sorted(low_log)
    low_log_path = os.path.join(path_to_test, f"low_log_{property}.csv")
    header = ["subtest", property]
    with open(low_log_path, "w", newline='') as low_log_file:
        writer = csv.writer(low_log_file)
        writer.writerow(header)
        for indScore in low_log:
            writer.writerow(indScore)


def run_three_test_kinds(path, plot_as_discrete):
    stats_for_whole_test(path, 'avgBestScore', plot_as_discrete)
    stats_for_whole_test(path, 'highestBestScore', plot_as_discrete)
    stats_for_whole_test(path, 'bestScores', plot_as_discrete)


for entry in os.scandir(get_measurements_dir()):
    # used to execute only for specific subtest
    # if "Feedback" not in entry.path:
    #     continue
    if contains_dirs(entry.path):
        print("Begin processing test: " + entry.path)
        run_three_test_kinds(entry.path, True)
        run_three_test_kinds(entry.path, False)
