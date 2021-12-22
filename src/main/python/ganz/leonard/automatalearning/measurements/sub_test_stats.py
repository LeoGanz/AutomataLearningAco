import matplotlib.pyplot as plt
import pandas as pd
import os
import common


def process_iteration(path_to_folder):
    path_segments = path_to_folder.split('\\')
    test_name = path_segments[len(path_segments) - 2]
    subtest_name = path_segments[len(path_segments) - 1]
    plt.figure()
    plt.title(test_name + ": " + subtest_name)
    plt.ylabel("Score (matched words / total words)")
    plt.xlabel("Number of processed colonies")
    plt.gca().set_ylim([0, 1.1])
    for csv_file in os.scandir(path_to_folder):
        csv_path = csv_file.path
        if csv_path.endswith('.csv'):
            print("reading: " + csv_path)
            csv_data = pd.read_csv(csv_path, sep=';', usecols=["colony", "score"])
            plt.plot(csv_data["colony"], csv_data["score"])
    fig = plt.gcf()
    plt.show()
    plot_path = path_to_folder + "/plot.png"
    print("writing plot to: " + plot_path)
    fig.savefig(plot_path)


for entry in os.scandir(common.get_measurements_dir()):
    print("Begin processing test: " + entry.path)
    if common.contains_dirs(entry.path):
        for inner_entry in os.scandir(entry.path):
            if os.path.isdir(inner_entry):
                process_iteration(inner_entry.path)
    else:
        # no subtests
        process_iteration(entry.path)
        break
