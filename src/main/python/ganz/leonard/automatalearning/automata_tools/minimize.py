import sys
from util import parse_dfa

print("minimization script starting")
if not len(sys.argv) == 6:
    raise Exception("Parameters have wrong format!")

dfa = parse_dfa(sys.argv[1:])
mini_dfa = dfa.reduce()

print(mini_dfa.__repr__())
print("script successful")
