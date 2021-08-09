import ast
from greenery import fsm


def parse_dfa(args):
    alphabet = ast.literal_eval(args[0])
    states = set(map(int, ast.literal_eval(args[1])))
    initial = int(args[2])
    finals = set(map(int, ast.literal_eval(args[3])))
    transitions = ast.literal_eval(args[4])
    return fsm.fsm(alphabet, states, initial, finals, transitions)
