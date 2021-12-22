import os
import json
import matplotlib.pyplot as plt
import common


def stats_for_whole_test(path_to_test, property):
    x_label_global = ""
    x_ticks = []
    X = []
    Y = []
    test_name = path_to_test.split('\\')[-1]
    for subtest in os.scandir(path_to_test):
        if os.path.isfile(subtest):
            continue
        stats_path = os.path.join(subtest, "stats.json")
        path_segments = stats_path.split('\\')
        subtest_name = path_segments[-2]
        subtest_name_segments = subtest_name.split(' ')
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

        print(subtest_name)
        with open(stats_path) as f:
            subtest_stats = json.load(f)
            Y.append(subtest_stats[property])

    X, Y, x_ticks = (list(t) for t in zip(*sorted(zip(X, Y, x_ticks))))
    plt.figure()
    plt.title(test_name)
    plt.ylabel(property + " in (matched words / total words)")
    plt.xlabel(x_label_global)
    every_nth = max(1, len(X) // 10)
    print(every_nth)
    for i in range(len(x_ticks)):
        if i % every_nth != 0:
            x_ticks[i] = None
    plt.xticks(X, x_ticks)
    plt.gca().set_ylim([0, 1.1])
    plt.plot(X, Y)
    fig = plt.gcf()
    plt.show()
    plot_path = path_to_test + "/stats_" + property + ".png"
    print("writing plot to: " + plot_path)
    fig.savefig(plot_path)


for entry in os.scandir(common.get_measurements_dir()):
    if common.contains_dirs(entry.path):
        print("Begin processing test: " + entry.path)
        stats_for_whole_test(entry.path, 'avgBestScore')
        stats_for_whole_test(entry.path, 'highestBestScore')
