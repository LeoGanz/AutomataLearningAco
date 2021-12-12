import sys
from util import parse_dfa
from greenery import lego

print("fsm to regex script starting")
if not len(sys.argv) == 6:
    raise Exception("Parameters have wrong format!")

dfa = parse_dfa(sys.argv[1:])
print(dfa.__repr__())
rx = lego.from_fsm(dfa).reduce()

print("regex: " + str(rx))
print("script successful")
