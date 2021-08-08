import sys
import ast
from greenery import fsm

print("minimization script starting")
if not len(sys.argv) == 6:
    raise Exception("Parameters have wrong format!")

alphabet = ast.literal_eval(sys.argv[1])
states = set(map(int, ast.literal_eval(sys.argv[2])))
initial = int(sys.argv[3])
finals = set(map(int, ast.literal_eval(sys.argv[4])))
transitions = ast.literal_eval(sys.argv[5])
dfa = fsm.fsm(alphabet, states, initial, finals, transitions)
mini_dfa = dfa.reduce()

print(mini_dfa.__repr__())
print("script successful")
