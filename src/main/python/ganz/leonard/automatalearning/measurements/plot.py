import matplotlib.pyplot as plt
import pandas as pd
import os


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
            print(csv_data)
            plt.plot(csv_data["colony"], csv_data["score"])
    fig = plt.gcf()
    plt.show()
    plot_path = path_to_folder + "/plot.png"
    print("writing plot to: " + plot_path)
    fig.savefig(plot_path)


script_dir = os.path.dirname(__file__)
project_dir = os.path.abspath(os.path.join(script_dir, "../../../../../../.."))
measurements_dir = os.path.join(project_dir, "measurements")

for entry in os.scandir(measurements_dir):
    print(entry.path)
    for inner_entry in os.scandir(entry.path):
        if os.path.isdir(inner_entry):
            process_iteration(inner_entry.path)
        else:
            process_iteration(entry.path)
            break
        #break  # tmp
    #break  # tmp
