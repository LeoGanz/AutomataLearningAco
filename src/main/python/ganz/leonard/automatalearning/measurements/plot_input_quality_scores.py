import matplotlib.pyplot as plt
import pandas as pd
import os
from common import *
import numpy as np
from collections import Counter

testname = "InputQuality"
# testname = "BalancingScores"
file = os.path.join(get_measurements_dir(), f"{testname}.csv")
print(f"Loading Scores from {file}")
csv_data = pd.read_csv(file, sep=',', dtype=np.float64)
Y = csv_data.to_records(index=False, column_dtypes=np.float64)
Y = list(map(tuple, Y))
print(Y)
X = range(len(Y))
fig, ax = plt.subplots()
ax.set_title("Scores for different a*b inputs")
ax.set_ylabel("Score (matched words / total words)")
ax.set_xlabel("Iterations")
ax.xaxis.get_major_locator().set_params(integer=True)
ax.set_ylim(NORMALIZED_Y_LIMIT)
labels = csv_data.columns
scatter_multival(X, Y, labels, ax, False)
ax.legend(loc="lower left")
plt.show()
plot_path = os.path.join(get_measurements_dir(), f"{testname}.png")
print("writing plot to: " + plot_path)
fig.savefig(plot_path)
plt.close(fig)


