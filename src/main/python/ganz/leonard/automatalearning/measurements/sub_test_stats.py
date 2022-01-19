import matplotlib.pyplot as plt
import pandas as pd
import os
from common import *
import json

SKIP_FOR_STABILITY = 2 / 3


def jitter_to_stability(jitter):
    if jitter < 0:
        raise ValueError("Jitter must be positive")
    return 1 / ((.01 * jitter + 1) ** 10)


def calc_jitter(csv_data, nr_colonies):
    prev_score = -1
    jitter = 0
    for score in csv_data['score'][round(SKIP_FOR_STABILITY * nr_colonies):]:
        if prev_score != -1 and prev_score != score:
            jitter += 1
        prev_score = score
    return jitter


def process_stability(csv_data, subtest_folder, it_id, nr_colonies):
    jitter = calc_jitter(csv_data, nr_colonies)
    stability = jitter_to_stability(jitter)

    stats_path = os.path.join(subtest_folder, FILENAME_STATS)
    with open(stats_path, 'r+') as f:
        subtest_stats = json.load(f)
        if KEY_STABILITIES not in subtest_stats:
            stability_json = {}
        else:
            stability_json = subtest_stats[KEY_STABILITIES]
        stability_json.update({it_id: stability})
        subtest_stats.update({KEY_STABILITIES: stability_json})
        f.seek(0)
        json.dump(subtest_stats, f, indent=4)
        f.truncate()


def calc_avg_stability(subtest_folder):
    stats_path = os.path.join(subtest_folder, FILENAME_STATS)
    with open(stats_path, 'r+') as f:
        subtest_stats = json.load(f)
        avg_stability = sum(subtest_stats[KEY_STABILITIES].values()) / len(subtest_stats[KEY_STABILITIES])
        subtest_stats.update({KEY_AVG_STABILITY: avg_stability})
        f.seek(0)
        json.dump(subtest_stats, f, indent=4)
        f.truncate()


def process_subtest(path_to_folder):
    path_segments = path_to_folder.split('\\')
    test_name = path_segments[len(path_segments) - 2]
    subtest_name = path_segments[len(path_segments) - 1]
    plt.figure()
    plt.title(test_name + ": " + subtest_name)
    plt.ylabel("Score (matched words / total words)")
    plt.xlabel("Number of processed colonies")
    plt.gca().set_ylim(NORMALIZED_Y_LIMIT)

    # Process single iteration
    for csv_file in os.scandir(path_to_folder):
        csv_path = csv_file.path
        if csv_path.endswith('.csv'):
            csv_data = pd.read_csv(csv_path, sep=';', usecols=["colony", "score"])
            plt.plot(csv_data["colony"], csv_data["score"])
            it_id = csv_path.split('\\')[-1][:-4]  # last elem of path; without .csv
            nr_colonies = len(csv_data) - 1
            process_stability(csv_data, path_to_folder, it_id, nr_colonies)

    # Finalize calculations and plotting
    calc_avg_stability(path_to_folder)
    fig = plt.gcf()
    # plt.show()
    plot_path = os.path.join(path_to_folder, "plot.png")
    print("writing plot to: " + plot_path)
    fig.savefig(plot_path)
    plt.close(fig)


for test in os.scandir(get_measurements_dir()):
    print("Begin processing test: " + test.path)
    # used to execute only for specific subtest
    # if "Feedback" not in test.path:
    #     continue
    if contains_dirs(test.path):
        for subtest in os.scandir(test.path):
            if os.path.isdir(subtest):
                process_subtest(subtest.path)
    else:
        # no subtests
        if os.path.isdir(test.path):
            process_subtest(test.path)
